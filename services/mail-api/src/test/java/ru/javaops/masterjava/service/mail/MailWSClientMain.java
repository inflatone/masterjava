package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableList;

public class MailWSClientMain {
    public static void main(String[] args) {
        MailWSClient.sendMail(
                ImmutableList.of(new Addressee("To <sane5ever@gmail.com>")),
                ImmutableList.of(),
                "Subject",
                "Body"
        );
    }
}
