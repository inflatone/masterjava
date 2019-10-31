package ru.javaops.masterjava.service.mail;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.WsClient;

import javax.xml.namespace.QName;
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

    public static String sendToGroup(final Set<Addressee> to, final Set<Addressee> cc, final String subject, final String body) {
        log.info("Send to group ('" + to + "') cc '" + cc + "' subject '" + subject
                + '\'' + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        var status = WS_CLIENT.getPort().sendToGroup(to, cc, subject, body);
        log.info("Send to group with status: " + status);
        return status;
    }

    public static GroupResult sendBulk(final Set<Addressee> to, final String subject, final String body) {
        log.info("Send bulk to '" + to + "' subject '" + subject + '\'' + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        var result = WS_CLIENT.getPort().sendBulk(to, subject, body);
        log.info("Sent bulk with result: " + result);
        return result;
    }

    public static Set<Addressee> split(String addressees) {
        Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(addressees);
        return ImmutableSet.copyOf(Iterables.transform(split, Addressee::new));
    }
}
