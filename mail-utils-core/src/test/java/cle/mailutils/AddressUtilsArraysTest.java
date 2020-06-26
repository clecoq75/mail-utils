package cle.mailutils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;

import static cle.mailutils.TestUtils.toAddressList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AddressUtilsArraysTest {
    @Parameterized.Parameters(name= "{index}: should be {2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { null, null, true },
                { new String[0], null, true },
                { null, new String[0], true },
                { new String[] { null }, null, true },
                { null, new String[] { null }, true },
                { new String[] { }, new String[] { }, true },
                { new String[] { "a@a.a", "b@b.b" }, new String[] { "a@a.a", "b@b.b" }, true },
                { new String[] { "a@a.a", "a@a.a", "a@a.a" }, new String[] { "a@a.a" }, true },
                { new String[] { "a@a.a" }, new String[] { "a@a.a", "a@a.a", "a@a.a" }, true },
                { new String[] { "b@b.b", "a@a.a" }, new String[] { "a@a.a", "b@b.b" }, true },
                { new String[] { "a@a.a", "b@b.b", "a@a.a" }, new String[] { "a@a.a", "b@b.b" }, true },
                { new String[] { "a@a.a", "b@b.b" }, new String[] { "a@a.a", "b@b.b", "a@a.a" }, true },
                { new String[] { "a@a.a", null, "b@b.b" }, new String[] { "a@a.a", "b@b.b", "a@a.a", null }, true },

                { new String[] { "a@a.a" }, new String[] { }, false },
                { new String[] { "a@a.a" }, new String[] { null }, false },
                { new String[] { null }, new String[] { "b@b.b" }, false },
                { new String[] { }, new String[] { "b@b.b" }, false },
                { new String[] { "a@a.a", "b@b.b" }, new String[] { "a@a.a", "a@a.a" }, false },
                { new String[] { "a@a.a", "b@b.b" }, new String[] { "a@a.a", "c@c.c", "b@b.b" }, false },
        });
    }

    private Address[] addressList1;
    private Address[] addressList2;
    private boolean expected;

    public AddressUtilsArraysTest(String[] list1, String[] list2, boolean expected) throws AddressException {
        addressList1 = toAddressList(list1);
        addressList2 = toAddressList(list2);
        this.expected = expected;
    }

    @Test
    public void test() {
        assertEquals(expected, AddressUtils.equals(addressList1, addressList2, false));
    }
}
