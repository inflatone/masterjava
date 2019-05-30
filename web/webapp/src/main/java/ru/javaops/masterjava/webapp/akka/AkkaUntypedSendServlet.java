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
import java.util.concurrent.TimeUnit;

import static ru.javaops.masterjava.webapp.WebUtil.createMailObject;
import static ru.javaops.masterjava.webapp.WebUtil.doAsync;
import static ru.javaops.masterjava.webapp.akka.AkkaWebappListener.akkaActivator;

@Slf4j
@MultipartConfig
@WebServlet(value = "/sendAkkaUntyped", loadOnStartup = 1)
public class AkkaUntypedSendServlet extends HttpServlet {
    private ActorRef mailActor;
    private ExecutorService executorService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mailActor = akkaActivator.getActorRef("akka.tcp://MailService@127.0.0.1:2553/user/mail-actor");
        executorService = Executors.newFixedThreadPool(8);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (executorService != null) {
            log.info("shutdown");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                    log.info("shutdownNow");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        doAsync(resp, () -> {
            MailObject mailObject = createMailObject(req);
            final AsyncContext aCtx = req.startAsync();
            executorService.submit(() -> sendAkka(mailObject, aCtx));
        });
    }

    private void sendAkka(MailObject mailObject, AsyncContext aCtx) {
        ActorRef webappActor = akkaActivator.startActor(Props.create(WebappActor.class, aCtx));
        mailActor.tell(mailObject, webappActor);

    }

    public static class WebappActor extends AbstractActor {
        private final AsyncContext aCtx;

        public WebappActor(AsyncContext aCtx) {
            this.aCtx = aCtx;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().match(
                    GroupResult.class,
                    groupResult -> {
                        log.info(groupResult.toString());
                        aCtx.getResponse().getWriter().write(groupResult.toString());
                        aCtx.complete();
                    }
            ).build();
        }
    }
}
