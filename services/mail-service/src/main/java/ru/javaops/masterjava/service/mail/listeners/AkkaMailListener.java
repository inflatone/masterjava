package ru.javaops.masterjava.service.mail.listeners;

import akka.japi.Creator;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.akka.AkkaActivator;
import ru.javaops.masterjava.service.mail.MailRemoteService;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class AkkaMailListener implements ServletContextListener {
    private AkkaActivator activator;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        activator = AkkaActivator.start("MailService", "mail-service");
        activator.startTypedActor(
                MailRemoteService.class, "mail-remote-service",
                (Creator<MailRemoteService>) () ->
                        mailObject -> MailServiceExecutor.sendAsyncWithReply(mailObject, activator.getExecutionContext())
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        activator.shutdown();

    }
}
