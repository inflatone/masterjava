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

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.javaops.masterjava.webapp.WebUtil.createMailObject;
import static ru.javaops.masterjava.webapp.WebUtil.doAsync;
import static ru.javaops.masterjava.webapp.akka.AkkaWebappListener.akkaActivator;

@Slf4j
@WebServlet(value = "/sendAkkaUntyped", loadOnStartup = 1, asyncSupported = true)
@MultipartConfig
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
        if (executorService != null) {
            log.info("executorService shutdown");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                    log.info("executorService shutdownNow");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                // do nothing
            }
        }
        super.destroy();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(UTF_8.name());
        doAsync(response, () -> {
            var mailObject = createMailObject(request);
            executorService.submit(() -> sendAkka(mailObject, request.startAsync()));
        });
    }

    private void sendAkka(MailObject mailObject, AsyncContext context) {
        var webappActor = akkaActivator.startActor(Props.create(WebappActor.class, context));
        mailActor.tell(mailObject, webappActor);
    }

    public static class WebappActor extends AbstractActor {
        private final AsyncContext asyncContext;

        public WebappActor(AsyncContext context) {
            this.asyncContext = context;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(GroupResult.class, this::process)
                    .build();
        }

        private void process(GroupResult groupResult) throws IOException {
            log.info("Receive result form mailActor: {}", groupResult);
            asyncContext.getResponse().getWriter().write(groupResult.toString());
            asyncContext.complete();
        }
    }
}
