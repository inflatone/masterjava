package ru.javaops.masterjava.webapp.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.javaops.masterjava.webapp.WebUtil.createMailObject;
import static ru.javaops.masterjava.webapp.WebUtil.doAsync;
import static ru.javaops.masterjava.webapp.akka.AkkaWebappListener.akkaActivator;

@Slf4j
@MultipartConfig
@WebServlet(value = "/sendAkkaUntyped", loadOnStartup = 1, asyncSupported = true)
public class AkkaUntypedSendServlet extends HttpServlet {
    private ActorRef mailActor;
    private ExecutorService executor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mailActor = akkaActivator.getActorRef("akka.tcp://MailService@127.0.0.1:2553/user/mail-actor");
        executor = Executors.newFixedThreadPool(8);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (executor != null) {
            log.info("executor: shutdown()");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, SECONDS)) {
                    log.info("executor: shutdownNow()");
                    executor.shutdownNow();
                }
            } catch (InterruptedException ignored) {

            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(UTF_8.name());
        doAsync(resp, () -> {
            MailObject mailObject = createMailObject(req);
            final AsyncContext asyncCtx = req.startAsync();
            executor.submit(() -> {
                ActorRef webappActor = akkaActivator.startActor(Props.create(WebappActor.class, asyncCtx));
                mailActor.tell(mailObject, webappActor);
            });
        });
    }

    public static class WebappActor extends AbstractActor {
        private final AsyncContext asyncCtx;

        public WebappActor(AsyncContext asyncCtx) {
            this.asyncCtx = asyncCtx;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().match(GroupResult.class,
                    groupResult -> {
                        log.info("Receive result from mailActor");
                        asyncCtx.getResponse().getWriter().write(groupResult.toString());
                        asyncCtx.complete();
                    }).build();
        }
    }
}
