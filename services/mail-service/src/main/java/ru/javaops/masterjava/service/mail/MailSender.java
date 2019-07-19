package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.mail.EmailException;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.mail.persist.MailCase;
import ru.javaops.masterjava.service.mail.persist.MailCaseDao;

import java.util.Set;

@Slf4j
public class MailSender {
    private static final MailCaseDao DAO = DBIProvider.getDao(MailCaseDao.class);

    static MailResult sendTo(Addressee to, String subject, String body) {
        val state = MailResult.OK;
        return new MailResult(to.getEmail(), state);
    }

    static String sendToGroup(Set<Addressee> to, Set<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + "\'" + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        String state = MailResult.OK;
        ;
        try {
            val email = MailConfig.createHtmlEmail();
            email.setSubject(subject);
            email.setHtmlMsg(body);
            for (Addressee addressee : to) {
                email.addTo(addressee.getEmail(), addressee.getName());
            }
            for (Addressee addressee : cc) {
                email.addCc(addressee.getEmail(), addressee.getName());
            }

            // https://yandex.ru/blog/company/66296
            email.setHeaders(ImmutableMap.of("List-Unsubscribe", "<mailto:sane5ever@yandex.ru?subject=Unsubscribe&body=Unsubscribe>"));
            email.send();
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
            state = e.getMessage();
        }
        DAO.insert(MailCase.of(to, cc, subject, body));
        log.info("Sent with state: " + state);
        return state;
    }
}
