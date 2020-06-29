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
        MimeMessage m1 = createMessages(false, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, d);
        MimeMessage m2 = createMessages(false, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(false, "no subject", new String[] {"b@a"}, new String[] {"c@c"}, null, d);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c"}, null, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForTo() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, null, d);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"D@D","c@c"}, null, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"d@d"}, null, d);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForCc() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, d);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"f@f"}, d);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testWithDisorderedFromAddresses() throws MessagingException {
        MimeMessage m1 = createMessages("a@a","B@B","c@c");
        MimeMessage m2 = createMessages("c@C","b@b","a@a");
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages("c@C","b@b","a@a","d@d");
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForDate() throws MessagingException {
        Date d1 = new Date();
        MimeMessage m1 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, d1);
        MimeMessage m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d1);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        Date d2 = new Date(System.currentTimeMillis()+60000L);
        m2 = createMessages(true, "no subject", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d2);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    @Test
    public void testFingerPrintWithForSubject() throws MessagingException {
        Date d = new Date();
        MimeMessage m1 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"e@e","f@f"}, d);
        MimeMessage m2 = createMessages(true, "Subject 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "SUBJECT 1", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, " SUBJECT 1 ", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages(true, "Subject 2", new String[] {"a@a"}, new String[] {"c@c","d@d"}, new String[] {"F@F","e@e"}, d);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    private MimeMessage createMessages(String... from) throws MessagingException {
        return createMessages(true, null, from, null, null, null);
    }

    private MimeMessage createMessages(boolean internetAddress, String subject, String[] from, String[] to, String[] cc, Date date) throws MessagingException {
        MimeMessage m = mock(MimeMessage.class);
        when(m.getSubject()).thenReturn(subject);
        when(m.getFrom()).thenReturn(toAddress(internetAddress, from));
        when(m.getRecipients(Message.RecipientType.TO)).thenReturn(toAddress(internetAddress, to));
        when(m.getRecipients(Message.RecipientType.CC)).thenReturn(toAddress(internetAddress, cc));
        when(m.getSentDate()).thenReturn(date);
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
