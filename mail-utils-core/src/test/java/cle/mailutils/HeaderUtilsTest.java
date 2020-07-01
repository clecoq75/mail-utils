package cle.mailutils;

import org.junit.Test;

import java.util.Set;

import static cle.mailutils.HeaderUtils.toSet;
import static org.junit.Assert.assertEquals;

public class HeaderUtilsTest {

    @Test
    public void testConstructor() {
        TestUtils.validateConstructorNotCallable(HeaderUtils.class);
    }

    @Test
    public void testNull() {
        Set<String> s = toSet(null);
        assertEquals(0, s.size());
    }

    @Test
    public void testEmpty() {
        Set<String> s = toSet(new String[] {});
        assertEquals(0, s.size());
    }

    @Test
    public void testSort() {
        Set<String> s = toSet(new String[] { "b", "a" });
        assertEquals(2, s.size());
        String[] array = s.toArray(new String[2]);
        assertEquals("a", array[0]);
        assertEquals("b", array[1]);
    }

    @Test
    public void testSortWithNullValues() {
        Set<String> s = toSet(new String[] { "b", null, "a", null });
        assertEquals(2, s.size());
        String[] array = s.toArray(new String[2]);
        assertEquals("a", array[0]);
        assertEquals("b", array[1]);
    }

    @Test
    public void testValuesAreTrimmed() {
        Set<String> s = toSet(new String[] { "a", " a", "a " });
        assertEquals(1, s.size());
        String[] array = s.toArray(new String[1]);
        assertEquals("a", array[0]);
    }

    @Test
    public void testCaseIsPreserved() {
        Set<String> s = toSet(new String[] { "a", "A" });
        assertEquals(2, s.size());
    }
}
