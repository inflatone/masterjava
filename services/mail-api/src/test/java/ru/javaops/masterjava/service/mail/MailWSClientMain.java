package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableList;

public class MailWSClientMain {
    public static void main(String[] args) {
        MailWSClient.sendMail(
                ImmutableList.of(new Addressee("To <sane5ever@ya.ru>")),
                ImmutableList.of(),
                "Java API",
                "Java 11 API"
        );
    }
}
