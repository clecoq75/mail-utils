package cle.mailutils;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import java.util.Objects;

public final class AddressUtils {

    private AddressUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean equals(Address address1, Address address2, boolean checkPersonal) {
        if (!checkPersonal) {
            return Objects.equals(address1, address2);
        }
        else if (address1==null) {
            return address2==null;
        }
        else if (address2==null) {
            return false;
        }
        else {
            if (address1 instanceof InternetAddress) {
                if (!(address2 instanceof InternetAddress)) {
                    return false;
                }
                else {
                    InternetAddress internetAddress1 = (InternetAddress) address1;
                    InternetAddress internetAddress2 = (InternetAddress) address2;
                    return Objects.equals(internetAddress1.getPersonal(), internetAddress2.getPersonal())
                            && Objects.equals(internetAddress1.getAddress(), internetAddress2.getAddress());
                }
            }
            else if (address2 instanceof InternetAddress) {
                return false;
            }
            else {
                return address1.equals(address2);
            }
        }
    }

    public static boolean equals(Address[] address1, Address[] address2, boolean checkPersonal) {
        address1 = address1==null? new Address[0] : address1;
        address2 = address2==null? new Address[0] : address2;
        boolean[] seen = new boolean[address2.length];

        // Initialize the "seen" arrays (null values are considered as "seen"
        // because we don't take them into account.
        for (int i=0; i<address2.length; i++) {
            seen[i] = address2[i]==null;
        }

        // Check that all values of address11 are contained in address2
        for (Address address : address1) {
            if (address != null && !searchAndMarkAsSeen(address, address2, seen, checkPersonal)) {
                return false;
            }
        }

        // Check that all entries of address2 are contained in address1
        for (boolean b : seen) {
            if (!b) {
                return false;
            }
        }

        return true;
    }

    private static boolean searchAndMarkAsSeen(Address address, Address[] address2, boolean[] seen, boolean checkPersonal) {
        boolean found = false;
        for (int i=0; i<address2.length; i++) {
            if (AddressUtils.equals(address, address2[i], checkPersonal)) {
                seen[i] = found = true;
            }
        }
        return found;
    }
}
