package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.mail.persist.MailCase;
import ru.javaops.masterjava.service.mail.persist.MailCaseDao;

import java.util.List;
import java.util.Map;

@Slf4j
public class MailSender {
    private static final MailCaseDao MAIL_CASE_DAO = DBIProvider.getDao(MailCaseDao.class);

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + '\''
                + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        var state = "OK";
        try {
            val email = prepareToSend(to, cc, subject, body);
            email.send();
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
            state = e.getMessage();
        }
        MAIL_CASE_DAO.insert(MailCase.of(to, cc, subject, state));
        log.info("Sent with state: " + state);
    }

    private static Email prepareToSend(List<Addressee> to, List<Addressee> cc, String subject, String body) throws EmailException {
        final var email = MailConfig.createHtmlEmail();
        email.setSubject(subject);
        email.setHtmlMsg(body);
        for (var addressee : to) {
            email.addTo(addressee.getEmail(), addressee.getName());
        }
        for (var addressee : cc) {
            email.addCc(addressee.getEmail(), addressee.getName());
        }
        // https://yandex.ru/blog/company/66296
        email.setHeaders(Map.of("List-Unsubscribe", "<mailto:sane4ever@ya.ru?subject=Unsubscribe&body=Unsubscribe>"));
        return email;
    }
}
