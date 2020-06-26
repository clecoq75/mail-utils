package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageComparatorSubjectTest {
    @Test
    public void testBothSubjectsAreNUll() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage[] messages = createMessages(null,null);
        assertTrue(messageComparator.subjectAreEquals(messages[0], messages[1]));
    }

    @Test
    public void testOneSubjectIsNUll() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage[] messages = createMessages("1234",null);
        assertFalse(messageComparator.subjectAreEquals(messages[0], messages[1]));
        assertFalse(messageComparator.subjectAreEquals(messages[1], messages[0]));
    }

    @Test
    public void testDifferentSubjects() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage[] messages = createMessages("1234", "4567");
        assertFalse(messageComparator.subjectAreEquals(messages[0], messages[1]));
        assertFalse(messageComparator.subjectAreEquals(messages[1], messages[0]));
    }

    @Test
    public void testSameSubjects() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        long current = System.currentTimeMillis();
        MimeMessage[] messages = createMessages("123","123");
        assertTrue(messageComparator.subjectAreEquals(messages[0], messages[1]));
        assertTrue(messageComparator.subjectAreEquals(messages[1], messages[0]));
    }

    public MimeMessage[] createMessages(String s1, String s2) throws MessagingException {
        Session session = Session.getInstance(new Properties());
        MimeMessage message1 = new MimeMessage(session);
        MimeMessage message2 = new MimeMessage(session);
        if (s1!=null) {
            message1.setSubject(s1);
        }
        if (s2!=null) {
            message2.setSubject(s2);
        }
        return Arrays.asList(message1, message2).toArray(new MimeMessage[2]);
    }
}
