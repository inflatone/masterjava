package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.WebStateException;
import ru.javaops.masterjava.web.WsClient;
import ru.javaops.masterjava.web.WsClient.HostConfig;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOMFeature;
import java.util.List;
import java.util.Set;

import static com.google.common.io.Resources.getResource;

@Slf4j
public class MailWSClient {
    private static final WsClient<MailService> WS_CLIENT;

    static {
        WS_CLIENT = new WsClient<>(
                getResource("wsdl/mailService.wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService"),
                MailService.class
        );
        WS_CLIENT.init("mail", "/mail/mailService?wsdl");
    }

    public static String sendToGroup(
            final Set<Addressee> to, final Set<Addressee> cc, final String subject, final String body, final List<Attachment> attachments
    ) throws WebStateException {
        log.info("Send to group ('" + to + "') cc '" + cc + "' subject '" + subject
                + '\'' + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        var status = getPort().sendToGroup(to, cc, subject, body, attachments);
        log.info("Send to group with status: " + status);
        return status;
    }

    public static GroupResult sendBulk(
            final Set<Addressee> to, final String subject, final String body, final List<Attachment> attachments
    ) throws WebStateException {
        log.info("Send bulk to '" + to + "' subject '" + subject + '\'' + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        var result = getPort().sendBulk(to, subject, body, attachments);
        log.info("Sent bulk with result: " + result);
        return result;
    }

    private static MailService getPort() {
        return WS_CLIENT.getPort(new MTOMFeature(1024));
    }

    public static HostConfig getHostConfig() {
        return WS_CLIENT.getHostConfig();
    }
}