package ru.javaops.masterjava.service.mail;

import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@Data
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
        email = email.trim();
        int index = email.indexOf('<');
        if (index == -1) {
            this.email = email;
        } else {
            this.name = email.substring(0, index).trim();
            this.email = email.substring(index + 1, email.length() - 1).trim();
        }
    }

    @Override
    public String toString() {
        return name == null ? email : name + " <" + email + '>';
    }
}
