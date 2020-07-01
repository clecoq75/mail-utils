package cle.mailutils;

import java.util.Set;
import java.util.TreeSet;

final class HeaderUtils {

    private HeaderUtils() {
        throw new IllegalStateException("Utility class");
    }

    static Set<String> toSet(String[] values) {
        Set<String> list = new TreeSet<>(String::compareTo);
        if (values!=null) {
            for (String value : values) {
                if (value != null) {
                    list.add(value.trim());
                }
            }
        }
        return list;
    }
}
