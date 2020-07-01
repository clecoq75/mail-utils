package cle.mailutils;

import org.junit.Test;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import static cle.mailutils.MessageFingerPrint.getFingerPrint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageFingerPrintTest {

    @Test
    public void testConstructor() {
        TestUtils.validateConstructorNotCallable(MessageFingerPrint.class);
    }

    @Test
    public void testIsSameWithSameMessages() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            assertEquals(getFingerPrint(in1), getFingerPrint(in2));
        }
    }

    @Test
    public void testIsSameWithSameMessages2() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            Session s = Session.getInstance(new Properties());
            assertEquals(getFingerPrint(new MimeMessage(s, in1)), getFingerPrint(new MimeMessage(s, in2)));
        }
    }

    @Test
    public void testIsSameWithDifferentMessages() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-3.eml")) {
            assertNotEquals(getFingerPrint(in1), getFingerPrint(in2));
        }
    }

    @Test
    public void testFingerPrintWithNonInternetAddress() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(false, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, null, d, null);
        MimeMessage m2 = createMessages(false, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(false, "no subject", new String[] {"b@a"}, new String[] {"c@c"}, null, null, d, null);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseFrom(false);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForTo() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"d@d","c@c"}, null, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"d@d"}, null, null, d, null);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseTo(false);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));
    }

    @Test
    public void testFingerPrintWithForCc() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"f@f","e@e"}, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"f@f"}, null, d, null);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseCc(false);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));
    }

    @Test
    public void testFingerPrintWithPersonalsOnFrom() throws MessagingException {
        testFingerPrintWithPersonals(null);
    }

    @Test
    public void testFingerPrintWithPersonalsOnTo() throws MessagingException {
        testFingerPrintWithPersonals(Message.RecipientType.TO);
    }

    @Test
    public void testFingerPrintWithPersonalsOnCc() throws MessagingException {
        testFingerPrintWithPersonals(Message.RecipientType.CC);
    }

    @Test
    public void testFingerPrintWithPersonalsOnBcc() throws MessagingException {
        testFingerPrintWithPersonals(Message.RecipientType.BCC);
    }

    public void testFingerPrintWithPersonals(Message.RecipientType type) throws MessagingException {
        MimeMessage m1 = createMessage("a@a", "Albert", type);
        MimeMessage m2 = createMessage("a@a", "Albert", type);

        MessageComparisonRules r = new MessageComparisonRules();
        if (type==Message.RecipientType.BCC) {
            r.setUseBcc(true);
        }

        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        r.setUsePersonals(true);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        m2 = createMessage("a@a", null, type);
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        m2 = createMessage("a@a", "Bill", type);
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        m2 = createMessage("a@a", "ALBERT", type);
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        r.setUsePersonals(false);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));
    }

    private MimeMessage createMessage(String address, String personal, Message.RecipientType type) throws MessagingException {
        MimeMessage m = mock(MimeMessage.class);
        InternetAddress a = mock(InternetAddress.class);
        when(a.getAddress()).thenReturn(address);
        when(a.getPersonal()).thenReturn(personal);
        if (type==null) {
            when(m.getFrom()).thenReturn(new Address[] { a });
        }
        else {
            when(m.getRecipients(type)).thenReturn(new Address[] { a });
        }
        return m;
    }

    @Test
    public void testFingerPrintWithBcc() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, new String[] {"g@g","h@h"}, d, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, new String[] {"g@g","i@i"}, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseBcc(true);
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, new String[] {"h@h", "g@g"}, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testWithDisorderedFromAddresses() throws MessagingException {
        MimeMessage m1 = createMessages("a@a","b@b","c@c");
        MimeMessage m2 = createMessages("c@c","b@b","a@a");
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages("c@C","b@b","a@a","d@d");
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintForDate() throws MessagingException {
        Date d1 = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        Date d2 = new Date(System.currentTimeMillis()+60000L);
        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d2, null);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseSentDate(false);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));
    }

    @Test
    public void testFingerPrintForReceivedDate() throws MessagingException {
        Date d1 = new Date();
        Date d2 = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, d2);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseReceivedDate(true);
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, d2);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForSubject() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "SUBJECT 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, " SUBJECT 1 ", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "Subject 2", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseSubject(false);
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));
    }

    @Test
    public void testFingerPrintForAdditionalHeaders() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);

        MessageComparisonRules r = new MessageComparisonRules();
        r.addAdditionalHeader("H1");
        when(m1.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        when(m2.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "vh2", "vh1" });
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "vh2", "vh3" });
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        r = new MessageComparisonRules();
        r.addAdditionalHeader("H1");
        r.addAdditionalHeader("H2");

        MessageComparisonRules r2 = new MessageComparisonRules();
        r2.addAdditionalHeader("H2");
        r2.addAdditionalHeader("H1");

        when(m1.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        when(m1.getHeader("H2")).thenReturn(new String[] { "vh2" });
        when(m2.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        when(m2.getHeader("H2")).thenReturn(new String[] { "vh2" });

        assertEquals(getFingerPrint(r, m1), getFingerPrint(r2, m2));

        when(m2.getHeader("H2")).thenReturn(new String[] { "vh3" });
        assertNotEquals(getFingerPrint(r, m1), getFingerPrint(r2, m2));
    }

    @Test
    public void testFingerPrintForAdditionalHeadersWithNull() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);

        MessageComparisonRules r = new MessageComparisonRules();
        r.addAdditionalHeader("H1");
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));

        when(m1.getHeader("H1")).thenReturn(new String[] { "vh1", null, "vh2" });
        when(m2.getHeader("H1")).thenReturn(new String[] { "vh2", "vh1", null, null });
        assertEquals(getFingerPrint(r, m1), getFingerPrint(r, m2));
    }

    private MimeMessage createMessages(String... from) throws MessagingException {
        return createMessages(true, null, from, null, null, null, null, null);
    }

    private MimeMessage createMessages(boolean internetAddress, String subject, String[] from, String[] to, String[] cc, String[] bcc, Date date, Date receivedDate) throws MessagingException {
        MimeMessage m = mock(MimeMessage.class);
        when(m.getSubject()).thenReturn(subject);
        when(m.getFrom()).thenReturn(toAddress(internetAddress, from));
        when(m.getRecipients(Message.RecipientType.TO)).thenReturn(toAddress(internetAddress, to));
        when(m.getRecipients(Message.RecipientType.CC)).thenReturn(toAddress(internetAddress, cc));
        when(m.getRecipients(Message.RecipientType.BCC)).thenReturn(toAddress(internetAddress, bcc));
        when(m.getSentDate()).thenReturn(date);
        when(m.getReceivedDate()).thenReturn(receivedDate);
        return m;
    }

    private Address[] toAddress(boolean internetAddress, String[] addresses) throws AddressException {
        if (addresses==null) {
            return new Address[] {};
        }
        else {
            Address[] list = new Address[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                list[i] = (internetAddress) ? new InternetAddress(addresses[i]) : new TestUtils.TestAddress(addresses[i]);
            }
            return list;
        }
    }
}
