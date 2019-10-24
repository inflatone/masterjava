package ru.javaops.masterjava.service.mail;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MailServiceClient {
    public static void main(String[] args) throws MalformedURLException {
        var service = Service.create(
                new URL("http://localhost:8080/mail/mailService?wsdl"),
                new QName("http://mail.service.masterjava.javaops.ru/", "MailServiceImplService")
        );
        var mailService = service.getPort(MailService.class);
        var email = new Addressee("sane4ever@ya.ru");
        var badEmail = new Addressee("Bad Email <bad_email.ru>");
        mailService.sendMail(
                List.of(email, badEmail), null, "subject", "body"
        );
    }
}