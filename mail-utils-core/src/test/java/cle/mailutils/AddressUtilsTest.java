package cle.mailutils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.mail.Address;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AddressUtilsTest {
    @Parameterized.Parameters(name= "{index}: equals(\"{0}\" <{1}>, \"{2}\" <{3}>, {4}) should be {6} (forceInternetAddress={5})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // Both are null
                { null, null, null, null, false, true, true },
                { null, null, null, null, true, true, true },
                { null, null, null, null, false, false, true },
                { null, null, null, null, true, false, true },

                // First one is null
                { null, null, null, "b@b.b", false, true, false },
                { null, null, null, "b@b.b", true, true, false },
                { null, null, null, "b@b.b", false, false, false },
                { null, null, null, "b@b.b", true, false, false },

                // Second one is null
                { null, "a@a.a", null, null, false, true, false },
                { null, "a@a.a", null, null, true, true, false },
                { null, "a@a.a", null, null, false, false, false },
                { null, "a@a.a", null, null, true, false, false },

                // Both are equals (no personals)
                { null, "a@a.a", null, "a@a.a", false, true, true },
                { null, "a@a.a", null, "a@a.a", true, true, true },
                { null, "a@a.a", null, "a@a.a", false, false, true },
                { null, "a@a.a", null, "a@a.a", true, false, true },

                // Both are equals (with same personals)
                { "a", "a@a.a", "a", "a@a.a", false, true, true },
                { "a", "a@a.a", "a", "a@a.a", true, true, true },
                { "a", "a@a.a", "a", "a@a.a", false, false, true },
                { "a", "a@a.a", "a", "a@a.a", true, false, true },

                // Both are different (with same personals)
                { "a", "a@a.a", "a", "b@b.b", false, true, false },
                { "a", "a@a.a", "a", "b@b.b", true, true, false },
                { "a", "a@a.a", "a", "b@b.b", false, false, false },
                { "a", "a@a.a", "a", "b@b.b", true, false, false },

                // Both are equals (but second personal is null)
                { "a", "a@a.a", null, "a@a.a", false, true, true },
                { "a", "a@a.a", null, "a@a.a", true, true, false },
                { "a", "a@a.a", null, "a@a.a", false, false, false },
                { "a", "a@a.a", null, "a@a.a", true, false, false },

                // Both are equals (but first personal is null)
                { null, "a@a.a", "b", "a@a.a", false, true, true },
                { null, "a@a.a", "b", "a@a.a", true, true, false },
                { null, "a@a.a", "b", "a@a.a", false, false, false },
                { null, "a@a.a", "b", "a@a.a", true, false, false },

                // Both are equals (with different personals)
                { "a", "a@a.a", "b", "a@a.a", false, true, true },
                { "a", "a@a.a", "b", "a@a.a", true, true, false },
                { "a", "a@a.a", "b", "a@a.a", false, false, true },
                { "a", "a@a.a", "b", "a@a.a", true, false, false }
        });
    }

    @BeforeClass
    public static void testConstructor() {
        TestUtils.validateConstructorNotCallable(AddressUtils.class);
    }

    private Address address1;
    private Address address2;
    private boolean checkPersonal;
    private boolean expected;

    public AddressUtilsTest(String personal1, String address1, String personal2, String address2, boolean checkPersonal, boolean forceInternetAddress, boolean expected) throws UnsupportedEncodingException {
        this.address1 = TestUtils.createAddress(personal1, address1, forceInternetAddress);
        this.address2 = TestUtils.createAddress(personal2, address2, forceInternetAddress);
        this.checkPersonal = checkPersonal;
        this.expected = expected;
    }

    @Test
    public void test() {
        assertEquals(expected, AddressUtils.equals(address1, address2, checkPersonal));
    }
}
