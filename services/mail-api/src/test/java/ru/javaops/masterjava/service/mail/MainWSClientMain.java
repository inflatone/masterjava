package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

public class MainWSClientMain {
    public static void main(String[] args) throws WebStateException, MalformedURLException {
        String state = MailWSClient.sendToGroup(
                Set.of(new Addressee("To <sane4ever@ya.ru>")),
                Set.of(),
                "Java 11 API sending",
                "is it okay?",
                List.of(new Attachment(
                        "version.html", new DataHandler(Configs.getConfigFile("version.html").toURI().toURL())))
        );
        System.out.println(state);
    }
}