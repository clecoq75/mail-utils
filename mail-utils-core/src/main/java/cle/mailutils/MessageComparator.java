package cle.mailutils;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import static javax.mail.Message.RecipientType.*;

public class MessageComparator {
    private boolean checkPersonals = false;
    private boolean checkTO = true;
    private boolean checkCC = true;
    private boolean checkBCC = false;

    public boolean isCheckPersonals() {
        return checkPersonals;
    }

    public void setCheckPersonals(boolean checkPersonals) {
        this.checkPersonals = checkPersonals;
    }

    public boolean isCheckTO() {
        return checkTO;
    }

    public void setCheckTO(boolean checkTO) {
        this.checkTO = checkTO;
    }

    public boolean isCheckCC() {
        return checkCC;
    }

    public void setCheckCC(boolean checkCC) {
        this.checkCC = checkCC;
    }

    public boolean isCheckBCC() {
        return checkBCC;
    }

    public void setCheckBCC(boolean checkBCC) {
        this.checkBCC = checkBCC;
    }

    public boolean isSame(InputStream inputStream1, InputStream inputStream2) throws MessagingException {
        Session s = Session.getInstance(new Properties());
        return isSame(new MimeMessage(s, inputStream1), new MimeMessage(s, inputStream2));
    }

    public boolean isSame(MimeMessage message1, MimeMessage message2) throws MessagingException {
        return sentDatesAreEquals(message1, message2)
                && fromAreEquals(message1, message2)
                && recipientsAreEquals(message1, message2)
                && subjectAreEquals(message1, message2);
    }

    boolean subjectAreEquals(MimeMessage message1, MimeMessage message2) throws MessagingException {
        String s1 = message1.getSubject();
        String s2 = message2.getSubject();
        return Objects.equals(s1, s2);
    }

    boolean sentDatesAreEquals(MimeMessage message1, MimeMessage message2) throws MessagingException {
        Date date1 = message1.getSentDate();
        Date date2 = message2.getSentDate();
        return Objects.equals(date1, date2);
    }

    boolean fromAreEquals(MimeMessage message1, MimeMessage message2) throws MessagingException {
        Address[] recipients1 = message1.getFrom();
        Address[] recipients2 = message2.getFrom();
        return AddressUtils.equals(recipients1, recipients2, checkPersonals);
    }

    boolean recipientsAreEquals(MimeMessage message1, MimeMessage message2) throws MessagingException {
        if (checkTO && recipientsAreNotEquals(message1, message2, TO)) {
            return false;
        }
        if (checkCC && recipientsAreNotEquals(message1, message2, CC)) {
            return false;
        }
        return !checkBCC || !recipientsAreNotEquals(message1, message2, BCC);
    }

    private boolean recipientsAreNotEquals(MimeMessage message1, MimeMessage message2, javax.mail.Message.RecipientType type) throws MessagingException {
        Address[] recipients1 = message1.getRecipients(type);
        Address[] recipients2 = message2.getRecipients(type);
        return !AddressUtils.equals(recipients1, recipients2, checkPersonals);
    }
}
