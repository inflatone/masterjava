package ru.javaops.masterjava.service.mail;

import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import java.util.List;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService", targetNamespace = "http://mail.javaops.ru/"
 //       , wsdlLocation = "WEB-INF/wsdl/mailService.wsdl"
)
public class MailServiceImpl implements MailService {
    @Override
    public void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        MailSender.sendMail(to, cc, subject, body);
        JAXBException e = new JAXBException("");
    }
}
