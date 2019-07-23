package ru.javaops.masterjava.service.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
