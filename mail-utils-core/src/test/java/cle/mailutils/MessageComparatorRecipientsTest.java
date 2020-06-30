package cle.mailutils;

import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static cle.mailutils.TestUtils.toAddressList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageComparatorRecipientsTest {
    @Test
    public void testWithoutRecipients() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        MimeMessage message1 = createMessage(null, null, null);
        MimeMessage message2 = createMessage(null, null, null);
        assertTrue(messageComparator.recipientsAreEquals(message1, message2));
    }

    @Test
    public void testTO() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        messageComparator.setCheckCC(false);
        MimeMessage message1 = createMessage(new String[] { "a@a.a", "b@b.b" }, new String[] { "c@c.c" }, null);
        MimeMessage message2 = createMessage(new String[] { "a@a.a", "b@b.b" }, new String[] { "d@d.d" }, null);
        assertTrue(messageComparator.recipientsAreEquals(message1, message2));

        messageComparator.setCheckCC(true);
        assertFalse(messageComparator.recipientsAreEquals(message1, message2));
    }

    @Test
    public void testCC() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        messageComparator.setCheckTO(false);
        messageComparator.setCheckCC(true);
        MimeMessage message1 = createMessage(new String[] { "c@c.c" }, new String[] { "a@a.a", "b@b.b" }, new String[] { "f@f.f" });
        MimeMessage message2 = createMessage(new String[] { "d@d.d", "b@b.b" }, new String[] { "a@a.a", "b@b.b" }, null);
        assertTrue(messageComparator.recipientsAreEquals(message1, message2));

        messageComparator.setCheckTO(true);
        assertFalse(messageComparator.recipientsAreEquals(message1, message2));

        messageComparator.setCheckTO(false);
        messageComparator.setCheckBCC(true);
        assertFalse(messageComparator.recipientsAreEquals(message1, message2));
    }

    @Test
    public void testBCC() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        messageComparator.setCheckTO(false);
        messageComparator.setCheckCC(false);
        messageComparator.setCheckBCC(true);
        MimeMessage message1 = createMessage(new String[] { "c@c.c" }, new String[] { "d@d.d" }, new String[] { "a@a.a", "b@b.b" });
        MimeMessage message2 = createMessage(new String[] { "f@f.f" }, new String[] { "e@e.e" }, new String[] { "a@a.a", "b@b.b" });
        assertTrue(messageComparator.recipientsAreEquals(message1, message2));

        messageComparator.setCheckTO(true);
        assertFalse(messageComparator.recipientsAreEquals(message1, message2));

        messageComparator.setCheckTO(false);
        messageComparator.setCheckCC(true);
        assertFalse(messageComparator.recipientsAreEquals(message1, message2));
    }

    public MimeMessage createMessage(String[] to, String[] cc, String[] bcc) throws MessagingException {
        Session session = Session.getInstance(new Properties());
        MimeMessage message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, toAddressList(to));
        message.setRecipients(Message.RecipientType.CC, toAddressList(cc));
        message.setRecipients(Message.RecipientType.BCC, toAddressList(bcc));
        return message;
    }
}
