package cle.mailutils;

import org.junit.Test;

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
}
