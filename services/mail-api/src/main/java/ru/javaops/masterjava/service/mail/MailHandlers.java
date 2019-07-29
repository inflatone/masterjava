package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.web.handler.SoapLoggingHandlers.ServerHandler;
import ru.javaops.masterjava.web.handler.SoapServerSecurityHandler;

public class MailHandlers {
    public static class SecurityHandler extends SoapServerSecurityHandler {
        public SecurityHandler() {
            super(MailWSClient.getHostConfig().authHeader);
        }
    }

    public static class LoggingHandler extends ServerHandler {
        public LoggingHandler() {
            super(MailWSClient.getHostConfig().serverDebugLevel);
        }
    }
}
