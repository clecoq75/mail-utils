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

public final class MessageFingerPrint {

    private static final byte[] separator = "¤".getBytes(StandardCharsets.UTF_8);
    private static final byte[] addresses_separator = ",".getBytes(StandardCharsets.UTF_8);

    private MessageFingerPrint() {
        throw new IllegalStateException("Utility class");
    }

    public static String getFingerPrint(InputStream inputStream) throws MessagingException {
        Session s = Session.getInstance(new Properties());
        return getFingerPrint(new MimeMessage(s, inputStream));
    }

    public static String getFingerPrint(MimeMessage message1) throws MessagingException {
        Date date = message1.getSentDate();
        Address[] from = message1.getFrom();
        Address[] to = message1.getRecipients(Message.RecipientType.TO);
        Address[] cc = message1.getRecipients(Message.RecipientType.CC);
        String subject = message1.getSubject();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        feedWithDate(baos, date);
        feedWithAddresses(baos, from);
        feedWithAddresses(baos, to);
        feedWithAddresses(baos, cc);
        feedWithSubject(baos, subject);

        return DigestUtils.md5Hex(baos.toByteArray());
    }

    private static void feedWithDate(ByteArrayOutputStream baos, Date date) {
        if (date!=null) {
            byte[] dateToBytes = ByteBuffer.allocate(8).putLong(date.getTime()).array();
            baos.write(dateToBytes, 0, dateToBytes.length);
        }

        feedWithSeparator(baos);
    }

    private static void feedWithAddresses(ByteArrayOutputStream baos, Address[] list) {
        if (list!=null && list.length>0) {
            Set<String> addresses = new TreeSet<>(String::compareTo);
            for (Address address : list) {
                if (address instanceof InternetAddress) {
                    addresses.add(((InternetAddress) address).getAddress().toLowerCase());
                } else {
                    addresses.add(address.toString().toLowerCase());
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
}
