package cle.mailutils;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static org.junit.Assert.fail;

public final class TestUtils {
    static class TestAddress extends Address {
        private final String address;

        public TestAddress(String address) {
            this.address = address;
        }

        @Override
        public String getType() {
            return "test";
        }

        @Override
        public String toString() {
            return address;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestAddress)) {
                return false;
            } else {
                TestAddress s = (TestAddress)o;
                return Objects.equals(this.address, s.address);
            }
        }
    }

    public static Address createAddress(String personal, String address, boolean forceInternetAddress) throws UnsupportedEncodingException {
        if (personal==null && address==null) {
            return null;
        }
        else if (personal==null && !forceInternetAddress) {
            return new TestAddress(address);
        }
        else {
            return new InternetAddress(address, personal);
        }
    }

    public static <T> void validateConstructorNotCallable(Class<T> valueType) {
        try {
            Constructor<T> constructor = valueType.getDeclaredConstructor();
            constructor.setAccessible(true);
            if (!checkInvocationTargetException(constructor)) {
                fail("An IllegalStateException must be thrown.");
            }
        } catch (NoSuchMethodException|InstantiationException|IllegalAccessException e) {
            fail("An unexpected exception has been thrown : "+e.getClass().getSimpleName()+" ("+e.getMessage()+")");
        }
    }

    private static <T> boolean checkInvocationTargetException(Constructor<T> constructor) throws IllegalAccessException, InstantiationException {
        try {
            constructor.newInstance();
        }
        catch (InvocationTargetException e) {
            if (e.getCause()!=null && e.getCause().getClass().equals(IllegalStateException.class)) {
                return true;
            }
        }
        return false;
    }

    public static Address[] toAddressList(String[] list) throws AddressException {
        if (list==null) {
            return null;
        }
        else {
            Address[] result = new Address[list.length];
            for (int i=0; i<list.length; i++) {
                if (list[i]!=null) {
                    result[i] = new InternetAddress(list[i]);
                }
            }
            return result;
        }
    }
}
