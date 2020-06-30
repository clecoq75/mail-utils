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
import java.util.*;

public final class MessageFingerPrint {

    private static final byte[] separator = "¤".getBytes(StandardCharsets.UTF_8);
    private static final byte[] addresses_separator = ",".getBytes(StandardCharsets.UTF_8);
    private static final byte[] header_values_separator = "~ù~".getBytes(StandardCharsets.UTF_8);

    private boolean useSentDate = true;
    private boolean useReceivedDate = false;
    private boolean useFrom = true;
    private boolean useTo = true;
    private boolean useCc = true;
    private boolean useBcc = false;
    private boolean useSubject = true;
    private final Set<String> additionalHeaders = new TreeSet<>(String::compareToIgnoreCase);

    public MessageFingerPrint() {

    }

    public boolean isUseSentDate() {
        return useSentDate;
    }

    public void setUseSentDate(boolean useSentDate) {
        this.useSentDate = useSentDate;
    }

    public boolean isUseReceivedDate() {
        return useReceivedDate;
    }

    public void setUseReceivedDate(boolean useReceivedDate) {
        this.useReceivedDate = useReceivedDate;
    }

    public boolean isUseFrom() {
        return useFrom;
    }

    public void setUseFrom(boolean useFrom) {
        this.useFrom = useFrom;
    }

    public boolean isUseTo() {
        return useTo;
    }

    public void setUseTo(boolean useTo) {
        this.useTo = useTo;
    }

    public boolean isUseCc() {
        return useCc;
    }

    public void setUseCc(boolean useCc) {
        this.useCc = useCc;
    }

    public boolean isUseBcc() {
        return useBcc;
    }

    public void setUseBcc(boolean useBcc) {
        this.useBcc = useBcc;
    }

    public boolean isUseSubject() {
        return useSubject;
    }

    public void setUseSubject(boolean useSubject) {
        this.useSubject = useSubject;
    }

    public void addAdditionalHeader(String header) {
        additionalHeaders.add(header);
    }

    public void removeAdditionalHeader(String header) {
        additionalHeaders.remove(header);
    }

    public Set<String> getAdditionalHeaders() {
        return Collections.unmodifiableSet(additionalHeaders);
    }

    public String getFingerPrint(InputStream inputStream) throws MessagingException {
        Session s = Session.getInstance(new Properties());
        return getFingerPrint(new MimeMessage(s, inputStream));
    }

    public String getFingerPrint(MimeMessage message1) throws MessagingException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (useSentDate) {
            feedWithDate(output, message1.getSentDate());
        }
        if (useReceivedDate) {
            feedWithDate(output, message1.getReceivedDate());
        }
        if (useFrom) {
            feedWithAddresses(output, message1.getFrom());
        }
        if (useTo) {
            feedWithAddresses(output, message1.getRecipients(Message.RecipientType.TO));
        }
        if (useCc) {
            feedWithAddresses(output, message1.getRecipients(Message.RecipientType.CC));
        }
        if (useBcc) {
            feedWithAddresses(output, message1.getRecipients(Message.RecipientType.BCC));
        }
        if (useSubject) {
            feedWithSubject(output, message1.getSubject());
        }
        for (String header : additionalHeaders) {
            feedWithStrings(output, message1.getHeader(header));
        }

        return DigestUtils.md5Hex(output.toByteArray());
    }

    private void feedWithDate(ByteArrayOutputStream baos, Date date) {
        if (date!=null) {
            byte[] dateToBytes = ByteBuffer.allocate(8).putLong(date.getTime()).array();
            baos.write(dateToBytes, 0, dateToBytes.length);
        }

        feedWithSeparator(baos);
    }

    private void feedWithAddresses(ByteArrayOutputStream baos, Address[] list) {
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

    private void feedWithSeparator(ByteArrayOutputStream baos) {
        baos.write(separator, 0, separator.length);
    }

    private void feedWithSubject(ByteArrayOutputStream baos, String subject) {
        if (subject!=null) {
            byte[] buffer = subject.trim().toLowerCase().getBytes(StandardCharsets.UTF_8);
            baos.write(buffer, 0, buffer.length);
        }

        feedWithSeparator(baos);
    }

    private void feedWithStrings(ByteArrayOutputStream baos, String[] values) {
        if (values!=null) {
            Set<String> list = new TreeSet<>(String::compareTo);
            for (String value : values) {
                if (value != null) {
                    list.add(value);
                }
            }
            for (String v : list) {
                byte[] buffer = v.trim().getBytes(StandardCharsets.UTF_8);
                baos.write(buffer, 0, buffer.length);
                baos.write(header_values_separator, 0, header_values_separator.length);
            }
        }

        feedWithSeparator(baos);
    }
}
