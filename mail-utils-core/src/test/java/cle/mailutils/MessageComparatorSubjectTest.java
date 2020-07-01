package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

import static cle.mailutils.MessageComparator.subjectAreEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageComparatorSubjectTest {
    @Test
    public void testBothSubjectsAreNUll() throws MessagingException {
        MimeMessage[] messages = createMessages(null,null);
        assertTrue(subjectAreEquals(new MessageComparisonRules(), messages[0], messages[1]));
    }

    @Test
    public void testOneSubjectIsNUll() throws MessagingException {
        MimeMessage[] messages = createMessages("1234",null);
        assertFalse(subjectAreEquals(new MessageComparisonRules(), messages[0], messages[1]));
        assertFalse(subjectAreEquals(new MessageComparisonRules(), messages[1], messages[0]));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseSubject(false);
        assertTrue(subjectAreEquals(r, messages[0], messages[1]));
        assertTrue(subjectAreEquals(r, messages[1], messages[0]));
    }

    @Test
    public void testDifferentSubjects() throws MessagingException {
        MimeMessage[] messages = createMessages("1234", "4567");
        assertFalse(subjectAreEquals(new MessageComparisonRules(), messages[0], messages[1]));
        assertFalse(subjectAreEquals(new MessageComparisonRules(), messages[1], messages[0]));

        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseSubject(false);
        assertTrue(subjectAreEquals(r, messages[0], messages[1]));
        assertTrue(subjectAreEquals(r, messages[1], messages[0]));
    }

    @Test
    public void testSameSubjects() throws MessagingException {
        MimeMessage[] messages = createMessages("123","123");
        assertTrue(subjectAreEquals(new MessageComparisonRules(), messages[0], messages[1]));
        assertTrue(subjectAreEquals(new MessageComparisonRules(), messages[1], messages[0]));
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
