package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;

public class MailWSClientMain {
    public static void main(String[] args) {
        MailWSClient.sendToGroup(
                ImmutableSet.of(new Addressee("To <sane5ever@ya.ru>")),
                ImmutableSet.of(),
                "Java API",
                "Java 11 API"
        );
    }
}
