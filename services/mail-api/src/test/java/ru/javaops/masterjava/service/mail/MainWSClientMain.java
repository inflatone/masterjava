package ru.javaops.masterjava.service.mail;

import java.util.List;

public class MainWSClientMain {
    public static void main(String[] args) {
        MailWSClient.sendMail(
                List.of(), List.of(), "Subject", "Body"
        );
    }
}
