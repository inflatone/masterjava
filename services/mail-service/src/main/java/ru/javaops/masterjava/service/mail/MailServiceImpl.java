package ru.javaops.masterjava.service.mail;

import javax.jws.WebService;
import java.util.List;

@WebService
public class MailServiceImpl implements MailService {
    @Override
    public void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        MailSender.sendMail(to, cc, subject, body);
    }
}
