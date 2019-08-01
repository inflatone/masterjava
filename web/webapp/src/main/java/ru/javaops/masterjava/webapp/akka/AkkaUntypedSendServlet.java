package ru.javaops.masterjava.webapp.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.javaops.masterjava.webapp.WebUtil.createMailObject;
import static ru.javaops.masterjava.webapp.WebUtil.doAndWriteResponse;

@Slf4j
@MultipartConfig
@WebServlet(value = "/sendAkkaUntyped", loadOnStartup = 1)
public class AkkaUntypedSendServlet extends HttpServlet {
    private ActorRef webappActor;
    private ActorRef mailActor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(UTF_8.name());
        doAndWriteResponse(resp, () -> sendAkka(createMailObject(req)));
    }

    private String sendAkka(MailObject mailObject) {
        mailActor.tell(mailObject, webappActor);
        return "Successfully sent AKKA message";
    }

    public static class WebappActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().match(GroupResult.class,
                    groupResult -> log.info(groupResult.toString())).build();
        }
    }
}
