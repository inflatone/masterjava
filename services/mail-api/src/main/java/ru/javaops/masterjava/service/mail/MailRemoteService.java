package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import scala.concurrent.Future;

public interface MailRemoteService {
    Future<GroupResult> sendBulk(MailObject mailObject);
}
