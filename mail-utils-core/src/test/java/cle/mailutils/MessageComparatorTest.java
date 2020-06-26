package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageComparatorTest {
    @Test
    public void testConstructorAndSetters() {
        MessageComparator mc = new MessageComparator();
        assertTrue(mc.isCheckTO());
        assertTrue(mc.isCheckCC());
        assertFalse(mc.isCheckBCC());
        assertFalse(mc.isCheckPersonals());

        mc.setCheckTO(false);
        assertFalse(mc.isCheckTO());
        mc.setCheckCC(false);
        assertFalse(mc.isCheckCC());
        mc.setCheckBCC(true);
        assertTrue(mc.isCheckBCC());
        mc.setCheckPersonals(true);
        assertTrue(mc.isCheckPersonals());
    }

    @Test
    public void testIsSameWithSameMessages() throws IOException, MessagingException {
        MessageComparator mc = new MessageComparator();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            assertTrue(mc.isSame(in1, in2));
        }

        mc = new MessageComparator();
        mc.setCheckPersonals(true);
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            assertFalse(mc.isSame(in1, in2));
        }
    }

    @Test
    public void testIsSameWithSameMessages2() throws IOException, MessagingException {
        MessageComparator mc = new MessageComparator();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-2.eml")) {
            Session s = Session.getInstance(new Properties());
            assertTrue(mc.isSame(new MimeMessage(s, in1), new MimeMessage(s, in2)));
        }
    }

    @Test
    public void testIsSameWithDifferentDates() throws IOException, MessagingException {
        MessageComparator mc = new MessageComparator();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-3.eml")) {
            assertFalse(mc.isSame(in1, in2));
        }
    }

    @Test
    public void testIsSameWithDifferentSubjects() throws IOException, MessagingException {
        MessageComparator mc = new MessageComparator();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-4.eml")) {
            assertFalse(mc.isSame(in1, in2));
        }
    }

    @Test
    public void testIsSameWithDifferentRecipients() throws IOException, MessagingException {
        MessageComparator mc = new MessageComparator();
        try (InputStream in1 = this.getClass().getResourceAsStream("basic-1.eml");
             InputStream in2 = this.getClass().getResourceAsStream("basic-5.eml")) {
            assertFalse(mc.isSame(in1, in2));
        }
    }
}
