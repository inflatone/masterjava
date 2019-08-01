package ru.javaops.masterjava.service.mail.listeners;

import akka.japi.Creator;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.akka.AkkaActivator;
import ru.javaops.masterjava.service.mail.MailRemoteService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import static ru.javaops.masterjava.service.mail.MailServiceExecutor.sendAsyncWithReply;

@Slf4j
@WebListener
public class AkkaMailListener implements ServletContextListener {
    private AkkaActivator akkaActivator;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        akkaActivator = AkkaActivator.start("MailService", "mail-service");
        akkaActivator.startTypedActor(MailRemoteService.class, "mail-remote-service",
                (Creator<MailRemoteService>) () ->
                        mailObject -> sendAsyncWithReply(mailObject, akkaActivator.getExecutionContext())
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        akkaActivator.shutdown();
    }
}
