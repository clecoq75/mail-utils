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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageFingerPrintTest {

    @Test
    public void testIsSameWithSameMessages() throws IOException, MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            assertEquals(mf.getFingerPrint(in1), mf.getFingerPrint(in2));
        }
    }

    @Test
    public void testIsSameWithSameMessages2() throws IOException, MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            Session s = Session.getInstance(new Properties());
            assertEquals(mf.getFingerPrint(new MimeMessage(s, in1)), mf.getFingerPrint(new MimeMessage(s, in2)));
        }
    }

    @Test
    public void testIsSameWithDifferentMessages() throws IOException, MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-3.eml")) {
            assertNotEquals(mf.getFingerPrint(in1), mf.getFingerPrint(in2));
        }
    }

    @Test
    public void testFingerPrintWithNonInternetAddress() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d = new Date();
        MimeMessage m1 = createMessages(false, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, null, d, null);
        MimeMessage m2 = createMessages(false, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(false, "no subject", new String[] {"b@a"}, new String[] {"c@c"}, null, null, d, null);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseFrom(false);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForTo() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"D@D","c@c"}, null, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"d@d"}, null, null, d, null);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseTo(false);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForCc() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"f@f"}, null, d, null);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseCc(false);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithBcc() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, new String[] {"g@g","h@h"}, d, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, new String[] {"g@g","i@i"}, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseBcc(true);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, new String[] {"h@h", "g@g"}, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testWithDisorderedFromAddresses() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        MimeMessage m1 = createMessages("a@a","B@B","c@c");
        MimeMessage m2 = createMessages("c@C","b@b","a@a");
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages("c@C","b@b","a@a","d@d");
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintForDate() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d1 = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, null);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d1, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        Date d2 = new Date(System.currentTimeMillis()+60000L);
        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d2, null);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseSentDate(false);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintForReceivedDate() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d1 = new Date();
        Date d2 = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, d2);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseReceivedDate(true);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d1, d2);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForSubject() throws MessagingException {
        MessageFingerPrint mf = new MessageFingerPrint();
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, null, d, null);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "SUBJECT 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, " SUBJECT 1 ", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d, null);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        m2 = createMessages(true, "Subject 2", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, null, d, null);
        assertNotEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));

        mf.setUseSubject(false);
        assertEquals(mf.getFingerPrint(m1), mf.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintForAdditionalHeaders() throws MessagingException {
        MessageFingerPrint mf1 = new MessageFingerPrint();

        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);

        mf1.addAdditionalHeader("H1");
        when(m1.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        when(m2.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        assertEquals(mf1.getFingerPrint(m1), mf1.getFingerPrint(m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "vh2", "vh1" });
        assertEquals(mf1.getFingerPrint(m1), mf1.getFingerPrint(m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "vh2", "vh3" });
        assertNotEquals(mf1.getFingerPrint(m1), mf1.getFingerPrint(m2));

        mf1 = new MessageFingerPrint();
        mf1.addAdditionalHeader("H1");
        mf1.addAdditionalHeader("H2");
        MessageFingerPrint mf2 = new MessageFingerPrint();
        mf2.addAdditionalHeader("H2");
        mf2.addAdditionalHeader("H1");

        when(m1.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        when(m1.getHeader("H2")).thenReturn(new String[] { "vh2" });
        when(m2.getHeader("H1")).thenReturn(new String[] { "vh1", "vh2" });
        when(m2.getHeader("H2")).thenReturn(new String[] { "vh2" });

        assertEquals(mf1.getFingerPrint(m1), mf2.getFingerPrint(m2));

        when(m2.getHeader("H2")).thenReturn(new String[] { "vh3" });
        assertNotEquals(mf1.getFingerPrint(m1), mf2.getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintForAdditionalHeadersWithNull() throws MessagingException {
        MessageFingerPrint mf1 = new MessageFingerPrint();

        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, null, d, null);

        mf1.addAdditionalHeader("H1");
        assertEquals(mf1.getFingerPrint(m1), mf1.getFingerPrint(m2));

        when(m1.getHeader("H1")).thenReturn(new String[] { "vh1", null, "vh2" });
        when(m2.getHeader("H1")).thenReturn(new String[] { "vh2", "vh1", null, null });
        assertEquals(mf1.getFingerPrint(m1), mf1.getFingerPrint(m2));
    }

    @Test
    public void testGettersAndSetters() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method[] methods = MessageFingerPrint.class.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("isUse") && method.getReturnType().getName().equals("boolean")) {
                Method set = MessageFingerPrint.class.getMethod("set" + method.getName().substring(2), boolean.class);

                MessageFingerPrint mf = new MessageFingerPrint();
                set.invoke(mf, true);
                assertTrue((boolean) method.invoke(mf));

                set.invoke(mf, false);
                assertFalse((boolean) method.invoke(mf));
            }
        }

        MessageFingerPrint mf = new MessageFingerPrint();
        assertEquals(0, mf.getAdditionalHeaders().size());

        mf.addAdditionalHeader("DOK");
        assertEquals(1, mf.getAdditionalHeaders().size());
        assertTrue(mf.getAdditionalHeaders().contains("DOK"));

        mf.addAdditionalHeader("dok");
        assertEquals(1, mf.getAdditionalHeaders().size());
        assertTrue(mf.getAdditionalHeaders().contains("DOK"));

        mf.removeAdditionalHeader("doK");
        assertEquals(0, mf.getAdditionalHeaders().size());
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
