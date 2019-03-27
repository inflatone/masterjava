package ru.javaops.masterjava.service.mail;

import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * gkislin
 * 15.11.2016
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email")
@XmlAccessorType(XmlAccessType.FIELD)
public class Addressee {
    @NonNull
    @XmlAttribute
    private String email;
    @XmlValue
    private String name;

    public Addressee(String email) {
        this(email, null);
        email = email.trim();
        int idx = email.indexOf('<');
        if (idx == -1) {
            this.email = email;
        } else {
            this.name = email.substring(0, idx).trim();
            this.email = email.substring(idx + 1, email.length() - 1).trim();
        }
    }

    public String toString() {
        return name == null ? email : String.format("%s <%s>", name, email);
    }
}
