package cle.mailutils;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class MessageComparisonRules {
    private boolean useSentDate = true;
    private boolean useReceivedDate = false;
    private boolean useFrom = true;
    private boolean useTo = true;
    private boolean useCc = true;
    private boolean useBcc = false;
    private boolean usePersonals = false;
    private boolean useSubject = true;
    private final Set<String> additionalHeaders = new TreeSet<>(String::compareToIgnoreCase);

    public boolean useSentDate() {
        return useSentDate;
    }

    public void setUseSentDate(boolean useSentDate) {
        this.useSentDate = useSentDate;
    }

    public boolean useReceivedDate() {
        return useReceivedDate;
    }

    public void setUseReceivedDate(boolean useReceivedDate) {
        this.useReceivedDate = useReceivedDate;
    }

    public boolean useFrom() {
        return useFrom;
    }

    public void setUseFrom(boolean useFrom) {
        this.useFrom = useFrom;
    }

    public boolean useTo() {
        return useTo;
    }

    public void setUseTo(boolean useTo) {
        this.useTo = useTo;
    }

    public boolean useCc() {
        return useCc;
    }

    public void setUseCc(boolean useCc) {
        this.useCc = useCc;
    }

    public boolean useBcc() {
        return useBcc;
    }

    public void setUseBcc(boolean useBcc) {
        this.useBcc = useBcc;
    }

    public boolean usePersonals() {
        return usePersonals;
    }

    public void setUsePersonals(boolean usePersonals) {
        this.usePersonals = usePersonals;
    }

    public boolean useSubject() {
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
}
