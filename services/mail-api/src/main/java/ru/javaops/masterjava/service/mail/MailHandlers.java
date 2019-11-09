package ru.javaops.masterjava.service.mail;


import ru.javaops.masterjava.web.handler.SoapServerSecurityHandler;

import static ru.javaops.masterjava.service.mail.MailWSClient.PASSWORD;
import static ru.javaops.masterjava.service.mail.MailWSClient.USER;

public class MailHandlers {
    public static class SecurityHandler extends SoapServerSecurityHandler {
        public SecurityHandler() {
            super(USER, PASSWORD);
        }
    }
}
