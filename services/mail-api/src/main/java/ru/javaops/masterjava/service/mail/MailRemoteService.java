package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;

public interface MailRemoteService {
    scala.concurrent.Future<GroupResult> sendBulk(MailObject mailObject);
}