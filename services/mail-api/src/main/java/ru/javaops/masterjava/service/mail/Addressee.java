package ru.javaops.masterjava.service.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Addressee {
    private @NonNull String email;
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
