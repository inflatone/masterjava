package ru.javaops.masterjava.webapp.akka;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailRemoteService;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.javaops.masterjava.webapp.WebUtil.createMailObject;
import static ru.javaops.masterjava.webapp.WebUtil.doAndWriteResponse;
import static ru.javaops.masterjava.webapp.akka.AkkaWebappListener.akkaActivator;

@Slf4j
@MultipartConfig
@WebServlet(value = "/sendAkkaTyped", loadOnStartup = 1, asyncSupported = true)
public class AkkaTypedSendServlet extends HttpServlet {
    private MailRemoteService mailService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mailService = akkaActivator.getTypedRef(
                MailRemoteService.class, "akka.tcp://MailService@127.0.0.1:2553/user/mail-remote-service");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(UTF_8.name());
        doAndWriteResponse(resp, () -> sendAkka(createMailObject(req)));
    }

    private String sendAkka(MailObject mailObject) throws Exception {
        scala.concurrent.Future<GroupResult> future = mailService.sendBulk(mailObject);
        log.info("Receive future, waiting for result...");
        GroupResult groupResult = Await.result(future, Duration.create(10, SECONDS));
        return groupResult.toString();
    }
}
