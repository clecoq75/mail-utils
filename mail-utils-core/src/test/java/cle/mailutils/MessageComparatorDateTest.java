package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;

import static cle.mailutils.MessageComparator.receivedDatesAreEquals;
import static cle.mailutils.MessageComparator.sentDatesAreEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageComparatorDateTest {
    @Test
    public void testBothSentDatesAreNUll() throws MessagingException {
        MimeMessage[] messages = createMessages(null,null, false);
        assertTrue(sentDatesAreEquals(getRules(false), messages[0], messages[1]));
    }

    @Test
    public void testBothReceivedDatesAreNUll() throws MessagingException {
        MimeMessage[] messages = createMessages(null,null, true);
        assertTrue(receivedDatesAreEquals(getRules(true), messages[0], messages[1]));
    }

    @Test
    public void testOneSentDateIsNUll() throws MessagingException {
        MimeMessage[] messages = createMessages(new Date(),null, false);
        assertFalse(sentDatesAreEquals(getRules(false), messages[0], messages[1]));
        assertFalse(sentDatesAreEquals(getRules(false), messages[1], messages[0]));
    }

    @Test
    public void testOneReceivedDateIsNUll() throws MessagingException {
        MimeMessage[] messages = createMessages(new Date(),null, true);
        assertFalse(receivedDatesAreEquals(getRules(true), messages[0], messages[1]));
        assertFalse(receivedDatesAreEquals(getRules(true), messages[1], messages[0]));
    }

    @Test
    public void testDifferentSentDates() throws MessagingException {
        MimeMessage[] messages = createMessages(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()+60000L), false);
        assertFalse(sentDatesAreEquals(getRules(false), messages[0], messages[1]));
        assertFalse(sentDatesAreEquals(getRules(false), messages[1], messages[0]));
    }

    @Test
    public void testDifferentReceivedDates() throws MessagingException {
        MimeMessage[] messages = createMessages(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()+60000L), true);
        assertFalse(receivedDatesAreEquals(getRules(true), messages[0], messages[1]));
        assertFalse(receivedDatesAreEquals(getRules(true), messages[1], messages[0]));
    }

    @Test
    public void testSameReceivedDates() throws MessagingException {
        testSameDates(true);
    }

    @Test
    public void testSameSentDates() throws MessagingException {
        testSameDates(false);
    }

    public void testSameDates(boolean isReceivedDate) throws MessagingException {
        long current = System.currentTimeMillis();
        Date date1 = new Date(current);
        Date date2 = new Date(current);
        MimeMessage[] messages = createMessages(date1,date2,isReceivedDate);
        if (isReceivedDate) {
            assertTrue(receivedDatesAreEquals(getRules(true), messages[0], messages[1]));
            assertTrue(receivedDatesAreEquals(getRules(true), messages[1], messages[0]));
        }
        else {
            assertTrue(sentDatesAreEquals(getRules(false), messages[0], messages[1]));
            assertTrue(sentDatesAreEquals(getRules(false), messages[1], messages[0]));
        }
    }

    private MessageComparisonRules getRules(boolean isReceivedDate) {
        MessageComparisonRules r = new MessageComparisonRules();
        if (isReceivedDate) {
            r.setUseReceivedDate(true);
        }
        else {
            r.setUseSentDate(true);
        }
        return r;
    }

    public MimeMessage[] createMessages(Date date1, Date date2, boolean isReceivedDate) throws MessagingException {
        MimeMessage message1 = mock(MimeMessage.class);
        MimeMessage message2 = mock(MimeMessage.class);
        if (isReceivedDate) {
            when(message1.getReceivedDate()).thenReturn(date1);
            when(message2.getReceivedDate()).thenReturn(date2);
        }
        else  {
            when(message1.getSentDate()).thenReturn(date1);
            when(message2.getSentDate()).thenReturn(date2);
        }
        return Arrays.asList(message1, message2).toArray(new MimeMessage[2]);
    }
}
