package cle.mailutils;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MessageComparisonRulesTest {
    @Test
    public void testGettersAndSetters() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method[] methods = MessageComparisonRules.class.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("use") && method.getReturnType().getName().equals("boolean")) {
                Method set = MessageComparisonRules.class.getMethod("setUse" + method.getName().substring(3), boolean.class);

                MessageComparisonRules rules = new MessageComparisonRules();
                set.invoke(rules, true);
                assertTrue((boolean) method.invoke(rules));

                set.invoke(rules, false);
                assertFalse((boolean) method.invoke(rules));
            }
        }

        MessageComparisonRules rules = new MessageComparisonRules();
        assertEquals(0, rules.getAdditionalHeaders().size());

        rules.addAdditionalHeader("DOK");
        assertEquals(1, rules.getAdditionalHeaders().size());
        assertTrue(rules.getAdditionalHeaders().contains("DOK"));

        rules.addAdditionalHeader("dok");
        assertEquals(1, rules.getAdditionalHeaders().size());
        assertTrue(rules.getAdditionalHeaders().contains("DOK"));

        rules.removeAdditionalHeader("doK");
        assertEquals(0, rules.getAdditionalHeaders().size());
    }
}
