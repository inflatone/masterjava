package ru.javaops.masterjava.service.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class GroupResult {
    /*
    number of successfully sent email
     */
    private int success;

    /**
     * failed emails with causes
     */
    private List<MailResult> failed;
    /**
     * global fail cause
     */
    private String failedCause;

    @Override
    public String toString() {
        return "Success: " + success + '\n' +
                (failed == null ? "" : "Failed: " + failed.toString());
    }
}
