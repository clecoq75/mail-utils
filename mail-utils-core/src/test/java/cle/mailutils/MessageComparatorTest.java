package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static cle.mailutils.MessageComparator.isSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
