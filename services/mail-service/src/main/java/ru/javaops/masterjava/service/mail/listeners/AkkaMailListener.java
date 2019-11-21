package ru.javaops.masterjava.service.mail.listeners;

import akka.actor.AbstractActor;
import akka.japi.Creator;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.akka.AkkaActivator;
import ru.javaops.masterjava.service.mail.MailRemoteService;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import ru.javaops.masterjava.web.WebStateException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class AkkaMailListener implements ServletContextListener {
    private AkkaActivator akkaActivator;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        akkaActivator = AkkaActivator.start("MailService", "mail-service");
        akkaActivator.startTypedActor(MailRemoteService.class, "mail-remote-service",
                (Creator<MailRemoteService>) () ->
                        mailObject -> MailServiceExecutor.sendAsyncWithReply(mailObject, akkaActivator.getExecutionContext())
        );
        akkaActivator.startActor(MailActor.class, "mail-actor");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        akkaActivator.shutdown();
    }

    public static class MailActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(MailObject.class, this::process)
                    .build();
        }

        private void process(MailObject mailObject) throws WebStateException {
            log.info("Receive mail from webappActor");
            var groupResult = MailServiceExecutor.sendBulk(mailObject);
            log.info("Send result to webappActor");
            sender().tell(groupResult, self());
        }
    }
}
