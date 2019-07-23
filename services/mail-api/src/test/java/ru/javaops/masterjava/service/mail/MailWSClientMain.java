package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import ru.javaops.masterjava.web.WebStateException;

public class MailWSClientMain {
    public static void main(String[] args) throws WebStateException {
        String state = MailWSClient.sendToGroup(
                ImmutableSet.of(new Addressee("To <sane5ever@ya.ru>")),
                ImmutableSet.of(),
                "Java API",
                "Java 11 API"
        );
        System.out.println(state);
    }
}
