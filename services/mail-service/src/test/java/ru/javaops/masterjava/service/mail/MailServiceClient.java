package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.web.WebStateException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class MailServiceClient {
    public static void main(String[] args) throws MalformedURLException, WebStateException {
        var service = Service.create(
                new URL("http://localhost:8080/mail/mailService?wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService")
        );
        var mailService = service.getPort(MailService.class);
        var email = new Addressee("sane4ever@ya.ru");
        var badEmail = new Addressee("Bad Email <bad_email.ru>");

        String state = mailService.sendToGroup(
                Set.of(email), null, "Group mail test", "Group mail body"
        );
        System.out.println("Group mail state: " + state);

        GroupResult groupResult = mailService.sendBulk(
                Set.of(email, badEmail), "Send bulk test", "Bulk mail body");
        System.out.println("\nBulk mail groupResult:\n" + groupResult);
    }
}