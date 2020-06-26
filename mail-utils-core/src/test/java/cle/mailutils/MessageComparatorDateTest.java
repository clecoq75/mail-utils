package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageComparatorDateTest {
    @Test
    public void testBothDatesAreNUll() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage[] messages = createMessages(null,null);
        assertTrue(messageComparator.sentDatesAreEquals(messages[0], messages[1]));
    }

    @Test
    public void testOneDateIsNUll() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage[] messages = createMessages(new Date(),null);
        assertFalse(messageComparator.sentDatesAreEquals(messages[0], messages[1]));
        assertFalse(messageComparator.sentDatesAreEquals(messages[1], messages[0]));
    }

    @Test
    public void testDifferentDates() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage[] messages = createMessages(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()+60000L));
        assertFalse(messageComparator.sentDatesAreEquals(messages[0], messages[1]));
        assertFalse(messageComparator.sentDatesAreEquals(messages[1], messages[0]));
    }

    @Test
    public void testSameDates() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        long current = System.currentTimeMillis();
        Date date1 = new Date(current);
        Date date2 = new Date(current);
        MimeMessage[] messages = createMessages(date1,date2);
        assertTrue(messageComparator.sentDatesAreEquals(messages[0], messages[1]));
        assertTrue(messageComparator.sentDatesAreEquals(messages[1], messages[0]));
    }

    public MimeMessage[] createMessages(Date date1, Date date2) throws MessagingException {
        Session session = Session.getInstance(new Properties());
        MimeMessage message1 = new MimeMessage(session);
        MimeMessage message2 = new MimeMessage(session);
        message1.setSentDate(date1);
        message2.setSentDate(date2);
        return Arrays.asList(message1, message2).toArray(new MimeMessage[2]);
    }
}
