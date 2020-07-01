package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import static cle.mailutils.MessageComparator.isSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageComparatorTest {
    @Test
    public void testConstructor() {
        TestUtils.validateConstructorNotCallable(MessageComparator.class);
    }

    @Test
    public void testIsSameWithSameMessages() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            assertTrue(isSame(in1, in2));
        }

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUsePersonals(true);
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            assertFalse(isSame(r, in1, in2));
        }
    }

    @Test
    public void testIsSameWithSameMessages2() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            Session s = Session.getInstance(new Properties());
            assertTrue(isSame(new MimeMessage(s, in1), new MimeMessage(s, in2)));
        }
    }

    @Test
    public void testIsSameWithDifferentDates() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-3.eml")) {
            assertFalse(isSame(in1, in2));
        }
    }

    @Test
    public void testIsSameWithDifferentSubjects() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-4.eml")) {
            assertFalse(isSame(in1, in2));
        }
    }

    @Test
    public void testIsSameWithDifferentRecipients() throws IOException, MessagingException {
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-5.eml")) {
            assertFalse(isSame(in1, in2));
        }
    }

    @Test
    public void testIsSameWithDifferentReceivedDate() throws IOException, MessagingException {
        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseSubject(false);
        r.setUseFrom(false);
        r.setUseSentDate(false);
        r.setUseReceivedDate(true);
        r.setUseBcc(false);
        r.setUseCc(false);
        r.setUseTo(false);

        Date d1 = new Date();
        MimeMessage m1 = mock(MimeMessage.class);
        when(m1.getReceivedDate()).thenReturn(d1);

        MimeMessage m2 = mock(MimeMessage.class);
        when(m2.getReceivedDate()).thenReturn(d1);

        assertTrue(isSame(r, m1, m2));

        when(m2.getReceivedDate()).thenReturn(new Date(System.currentTimeMillis()+60000L));
        assertFalse(isSame(r, m1, m2));
    }

    @Test
    public void testIsSameWithDifferentHeaders() throws IOException, MessagingException {
        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseSubject(false);
        r.setUseFrom(false);
        r.setUseSentDate(false);
        r.setUseReceivedDate(false);
        r.setUseBcc(false);
        r.setUseCc(false);
        r.setUseTo(false);
        r.addAdditionalHeader("H1");

        Date d1 = new Date();
        MimeMessage m1 = mock(MimeMessage.class);
        when(m1.getHeader("H1")).thenReturn(new String[] { "v1" });

        MimeMessage m2 = mock(MimeMessage.class);
        when(m2.getHeader("H1")).thenReturn(new String[] { "v1" });

        assertTrue(isSame(r, m1, m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "v1", "v2" });
        assertFalse(isSame(r, m1, m2));
    }
}
