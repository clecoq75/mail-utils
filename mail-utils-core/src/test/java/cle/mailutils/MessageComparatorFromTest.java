package cle.mailutils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;

import static cle.mailutils.TestUtils.createAddress;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MessageComparatorFromTest {

    @Parameterized.Parameters(name= "{index}: fromAreEquals(\"{0}\" <{1}>, \"{2}\" <{3}>) should be {5} (checkPersonals={4})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // Both are null
                { null, null, null, null, false, true },
                { null, null, null, null, true, true },

                // First one is null
                { null, null, null, "b@b.b", false, false },
                { null, null, null, "b@b.b", true, false },

                // Second one is null
                { null, "a@a.a", null, null, false, false },
                { null, "a@a.a", null, null, true, false },

                // Both are equals (same personals)
                { "a", "a@a.a", "a", "a@a.a", false, true },
                { "a", "a@a.a", "a", "a@a.a", true, true },

                // Both are equals (different personals)
                { "b", "a@a.a", "a", "a@a.a", false, true },
                { "b", "a@a.a", "a", "a@a.a", true, false },

                // Both are equals (first personal is null)
                { null, "a@a.a", "a", "a@a.a", false, true },
                { null, "a@a.a", "a", "a@a.a", true, false },

                // Both are equals (second personal is null)
                { "a", "a@a.a", null, "a@a.a", false, true },
                { "a", "a@a.a", null, "a@a.a", true, false },

                // Both are equals (both personals are null)
                { null, "a@a.a", null, "a@a.a", false, true },
                { null, "a@a.a", null, "a@a.a", true, true },
        });
    }

    private MimeMessage message1;
    private MimeMessage message2;
    private boolean checkPersonals;
    private boolean expected;

    public MessageComparatorFromTest(String personal1, String address1, String personal2, String address2, boolean checkPersonal, boolean expected) throws UnsupportedEncodingException, MessagingException {
        message1 = createMessages(personal1, address1);
        message2 = createMessages(personal2, address2);
        this.checkPersonals = checkPersonal;
        this.expected = expected;
    }

    @Test
    public void test() throws MessagingException {
        MessageComparator messageComparator = new MessageComparator();
        messageComparator.setCheckPersonals(checkPersonals);
        assertEquals(expected, messageComparator.fromAreEquals(message1, message2));
    }

    public MimeMessage createMessages(String displayName1, String email1) throws MessagingException, UnsupportedEncodingException {
        Session session = Session.getInstance(new Properties());
        MimeMessage message = new MimeMessage(session);
        Address address = createAddress(displayName1, email1, true);
        if (address!=null) {
            message.addFrom(new Address[]{ address });
        }
        return message;
    }
}
