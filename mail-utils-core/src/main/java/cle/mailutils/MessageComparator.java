package cle.mailutils;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import static cle.mailutils.HeaderUtils.toSet;
import static javax.mail.Message.RecipientType.*;

public final class MessageComparator {

    private static final MessageComparisonRules default_rules = new MessageComparisonRules();

    private MessageComparator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isSame(InputStream inputStream1, InputStream inputStream2) throws MessagingException {
        return isSame(null, inputStream1, inputStream2);
    }

    public static boolean isSame(MessageComparisonRules rules, InputStream inputStream1, InputStream inputStream2) throws MessagingException {
        Session s = Session.getInstance(new Properties());
        return isSame(rules, new MimeMessage(s, inputStream1), new MimeMessage(s, inputStream2));
    }

    public static boolean isSame(MimeMessage message1, MimeMessage message2) throws MessagingException {
        return isSame(null, message1, message2);
    }

    public static boolean isSame(MessageComparisonRules messageComparisonRules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        MessageComparisonRules rules = messageComparisonRules!=null? messageComparisonRules : default_rules;
        return sentDatesAreEquals(rules, message1, message2)
                && receivedDatesAreEquals(rules, message1, message2)
                && fromAreEquals(rules, message1, message2)
                && recipientsAreEquals(rules, message1, message2)
                && subjectAreEquals(rules, message1, message2)
                && additionalHeadersAreEquals(rules, message1, message2);
    }

    static boolean additionalHeadersAreEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        for (String header : rules.getAdditionalHeaders()) {
            Set<String> v1 = toSet(message1.getHeader(header));
            Set<String> v2 = toSet(message2.getHeader(header));
            if (!v1.equals(v2)) {
                return false;
            }
        }

        return true;
    }

    static boolean subjectAreEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        String s1 = message1.getSubject();
        String s2 = message2.getSubject();
        return !rules.useSubject() || Objects.equals(s1, s2);
    }

    static boolean sentDatesAreEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        Date date1 = message1.getSentDate();
        Date date2 = message2.getSentDate();
        return !rules.useSentDate() || Objects.equals(date1, date2);
    }

    static boolean receivedDatesAreEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        Date date1 = message1.getReceivedDate();
        Date date2 = message2.getReceivedDate();
        return !rules.useReceivedDate() || Objects.equals(date1, date2);
    }

    static boolean fromAreEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        Address[] recipients1 = message1.getFrom();
        Address[] recipients2 = message2.getFrom();
        return !rules.useFrom() || AddressUtils.equals(recipients1, recipients2, rules.usePersonals());
    }

    static boolean recipientsAreEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2) throws MessagingException {
        if (rules.useTo() && recipientsAreNotEquals(rules, message1, message2, TO)) {
            return false;
        }
        if (rules.useCc() && recipientsAreNotEquals(rules, message1, message2, CC)) {
            return false;
        }
        return !rules.useBcc() || !recipientsAreNotEquals(rules, message1, message2, BCC);
    }

    private static boolean recipientsAreNotEquals(MessageComparisonRules rules, MimeMessage message1, MimeMessage message2, javax.mail.Message.RecipientType type) throws MessagingException {
        Address[] recipients1 = message1.getRecipients(type);
        Address[] recipients2 = message2.getRecipients(type);
        return !AddressUtils.equals(recipients1, recipients2, rules.usePersonals());
    }
}
