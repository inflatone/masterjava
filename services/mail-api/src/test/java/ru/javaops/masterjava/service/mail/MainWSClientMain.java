package ru.javaops.masterjava.service.mail;

import java.util.List;

public class MainWSClientMain {
    public static void main(String[] args) {
        MailWSClient.sendMail(
                List.of(new Addressee("To <sane4ever@ya.ru>")),
                List.of(),
                "Java 11 API sending",
                "is it okay?"
        );
    }
}