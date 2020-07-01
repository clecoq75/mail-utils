package cle.mailutils;

import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static cle.mailutils.MessageComparator.additionalHeadersAreEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageComparatorAdditionalHeadersTest {
    @Test
    public void testNullValues() throws MessagingException {
        MimeMessage m1 = mock(MimeMessage.class);
        MimeMessage m2 = mock(MimeMessage.class);

        MessageComparisonRules r = new MessageComparisonRules();
        r.addAdditionalHeader("H1");
        assertTrue(additionalHeadersAreEquals(r, m1, m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "v1" });
        assertFalse(additionalHeadersAreEquals(r, m1, m2));
        assertFalse(additionalHeadersAreEquals(r, m2, m1));
    }

    @Test
    public void testSameValues() throws MessagingException {
        MimeMessage m1 = mock(MimeMessage.class);
        when(m1.getHeader("H1")).thenReturn(new String[] { "v1", "v2" });

        MimeMessage m2 = mock(MimeMessage.class);
        when(m2.getHeader("H1")).thenReturn(new String[] { "v1", "v2" });

        MessageComparisonRules r = new MessageComparisonRules();
        r.addAdditionalHeader("H1");

        assertTrue(additionalHeadersAreEquals(r, m1, m2));

        when(m2.getHeader("H1")).thenReturn(new String[] { "v2", null, "v1", " v1" });
        assertTrue(additionalHeadersAreEquals(r, m1, m2));
    }
}
