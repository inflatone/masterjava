package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.web.WebStateException;

import java.util.Set;

public class MainWSClientMain {
    public static void main(String[] args) throws WebStateException {
        String state = MailWSClient.sendToGroup(
                Set.of(new Addressee("To <sane4ever@ya.ru>")),
                Set.of(),
                "Java 11 API sending",
                "is it okay?"
        );
        System.out.println(state);
    }
}