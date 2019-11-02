package ru.javaops.masterjava.service.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class MailResult {
    public static final String OK = "OK";

    @NonNull
    @XmlAttribute
    private String email;

    @XmlValue
    private String result;

    public boolean isOk() {
        return OK.equals(result);
    }

    @Override
    public String toString() {
        return '\'' + email + "' result: " + result;
    }
}
