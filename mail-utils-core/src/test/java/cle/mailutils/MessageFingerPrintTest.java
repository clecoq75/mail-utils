package cle.mailutils;

import org.junit.Test;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
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
        MimeMessage m1 = createMessages("a@a", false);
        MimeMessage m2 = createMessages("a@a", false);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages("b@a", false);
        assertNotEquals(getFingerPrint(m1), getFingerPrint(m2));

        m2 = createMessages("a@a", true);
        assertEquals(getFingerPrint(m1), getFingerPrint(m2));
    }

    public MimeMessage createMessages(String address1, boolean internetAddress) throws MessagingException {
        MimeMessage m = mock(MimeMessage.class);
        if (internetAddress) {
            when(m.getFrom()).thenReturn(new Address[]{new InternetAddress(address1)});
        }
        else {
            when(m.getFrom()).thenReturn(new Address[]{new TestUtils.TestAddress(address1)});
        }
        return m;
    }
}
