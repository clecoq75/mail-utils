package cle.mailutils;

import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static cle.mailutils.MessageComparator.recipientsAreEquals;
import static cle.mailutils.TestUtils.toAddressList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageComparatorRecipientsTest {
    @Test
    public void testWithoutRecipients() throws MessagingException {
        MimeMessage message1 = createMessage(null, null, null);
        MimeMessage message2 = createMessage(null, null, null);
        assertTrue(recipientsAreEquals(new MessageComparisonRules(), message1, message2));
    }

    @Test
    public void testTO() throws MessagingException {
        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseCc(false);
        MimeMessage message1 = createMessage(new String[] { "a@a.a", "b@b.b" }, new String[] { "c@c.c" }, null);
        MimeMessage message2 = createMessage(new String[] { "a@a.a", "b@b.b" }, new String[] { "d@d.d" }, null);
        assertTrue(recipientsAreEquals(r, message1, message2));

        r.setUseCc(true);
        assertFalse(recipientsAreEquals(r, message1, message2));
    }

    @Test
    public void testCC() throws MessagingException {
        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseTo(false);
        r.setUseCc(true);
        MimeMessage message1 = createMessage(new String[] { "c@c.c" }, new String[] { "a@a.a", "b@b.b" }, new String[] { "f@f.f" });
        MimeMessage message2 = createMessage(new String[] { "d@d.d", "b@b.b" }, new String[] { "a@a.a", "b@b.b" }, null);
        assertTrue(recipientsAreEquals(r, message1, message2));

        r.setUseTo(true);
        assertFalse(recipientsAreEquals(r, message1, message2));

        r.setUseTo(false);
        r.setUseBcc(true);
        assertFalse(recipientsAreEquals(r, message1, message2));
    }

    @Test
    public void testBCC() throws MessagingException {
        MessageComparisonRules r = new MessageComparisonRules();
        r.setUseTo(false);
        r.setUseCc(false);
        r.setUseBcc(true);
        MimeMessage message1 = createMessage(new String[] { "c@c.c" }, new String[] { "d@d.d" }, new String[] { "a@a.a", "b@b.b" });
        MimeMessage message2 = createMessage(new String[] { "f@f.f" }, new String[] { "e@e.e" }, new String[] { "a@a.a", "b@b.b" });
        assertTrue(recipientsAreEquals(r, message1, message2));

        r.setUseTo(true);
        assertFalse(recipientsAreEquals(r, message1, message2));

        r.setUseTo(false);
        r.setUseCc(true);
        assertFalse(recipientsAreEquals(r, message1, message2));
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
