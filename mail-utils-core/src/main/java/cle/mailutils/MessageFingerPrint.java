package cle.mailutils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import static cle.mailutils.HeaderUtils.toSet;

public final class MessageFingerPrint {

    private static final byte[] separator = "¤".getBytes(StandardCharsets.UTF_8);
    private static final byte[] addresses_separator = ",".getBytes(StandardCharsets.UTF_8);
    private static final byte[] header_values_separator = "~ù~".getBytes(StandardCharsets.UTF_8);
    private static final MessageComparisonRules default_rules = new MessageComparisonRules();

    private MessageFingerPrint() {
        throw new IllegalStateException("Utility class");
    }

    public static String getFingerPrint(InputStream inputStream) throws MessagingException {
        return getFingerPrint(null, inputStream);
    }

    public static String getFingerPrint(MessageComparisonRules messageComparisonRules, InputStream inputStream) throws MessagingException {
        Session s = Session.getInstance(new Properties());
        return getFingerPrint(messageComparisonRules, new MimeMessage(s, inputStream));
    }

    public static String getFingerPrint(MimeMessage message1) throws MessagingException {
        return getFingerPrint(null, message1);
    }

    public static String getFingerPrint(MessageComparisonRules messageComparisonRules, MimeMessage message1) throws MessagingException {
        MessageComparisonRules rules = messageComparisonRules!=null? messageComparisonRules : default_rules;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (rules.useSentDate()) {
            feedWithDate(output, message1.getSentDate());
        }
        if (rules.useReceivedDate()) {
            feedWithDate(output, message1.getReceivedDate());
        }
        if (rules.useFrom()) {
            feedWithAddresses(output, message1.getFrom(), rules.usePersonals());
        }
        if (rules.useTo()) {
            feedWithAddresses(output, message1.getRecipients(Message.RecipientType.TO), rules.usePersonals());
        }
        if (rules.useCc()) {
            feedWithAddresses(output, message1.getRecipients(Message.RecipientType.CC), rules.usePersonals());
        }
        if (rules.useBcc()) {
            feedWithAddresses(output, message1.getRecipients(Message.RecipientType.BCC), rules.usePersonals());
        }
        if (rules.useSubject()) {
            feedWithSubject(output, message1.getSubject());
        }
        for (String header : rules.getAdditionalHeaders()) {
            feedWithStrings(output, message1.getHeader(header));
        }

        return DigestUtils.md5Hex(output.toByteArray());
    }

    private static void feedWithDate(ByteArrayOutputStream baos, Date date) {
        if (date!=null) {
            byte[] dateToBytes = ByteBuffer.allocate(8).putLong(date.getTime()).array();
            baos.write(dateToBytes, 0, dateToBytes.length);
        }

        feedWithSeparator(baos);
    }

    private static void feedWithAddresses(ByteArrayOutputStream baos, Address[] list, boolean usePersonals) {
        if (list!=null && list.length>0) {
            Set<String> addresses = new TreeSet<>(String::compareTo);
            for (Address address : list) {
                if (address instanceof InternetAddress) {
                    if (usePersonals) {
                        InternetAddress a = (InternetAddress)address;
                        String p = a.getPersonal();
                        addresses.add((p!=null? p+" " : "")+"<"+((InternetAddress)address).getAddress()+">");
                    }
                    else {
                        addresses.add(((InternetAddress)address).getAddress());
                    }
                } else {
                    addresses.add(address.toString());
                }
            }
            addresses.forEach(string -> {
                byte[] buffer = string.getBytes(StandardCharsets.UTF_8);
                baos.write(buffer, 0, buffer.length);
                baos.write(addresses_separator, 0, addresses_separator.length);
            });
        }

        feedWithSeparator(baos);
    }

    private static void feedWithSeparator(ByteArrayOutputStream baos) {
        baos.write(separator, 0, separator.length);
    }

    private static void feedWithSubject(ByteArrayOutputStream baos, String subject) {
        if (subject!=null) {
            byte[] buffer = subject.trim().toLowerCase().getBytes(StandardCharsets.UTF_8);
            baos.write(buffer, 0, buffer.length);
        }

        feedWithSeparator(baos);
    }

    private static void feedWithStrings(ByteArrayOutputStream baos, String[] values) {
        if (values!=null) {
            Set<String> list = toSet(values);
            for (String v : list) {
                byte[] buffer = v.trim().getBytes(StandardCharsets.UTF_8);
                baos.write(buffer, 0, buffer.length);
                baos.write(header_values_separator, 0, header_values_separator.length);
            }
        }

        feedWithSeparator(baos);
    }
}
