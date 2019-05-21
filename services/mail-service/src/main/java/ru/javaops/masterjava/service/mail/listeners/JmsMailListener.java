package ru.javaops.masterjava.service.mail.listeners;

import lombok.extern.slf4j.Slf4j;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class JmsMailListener implements ServletContextListener {
    private Thread listenerThread;
    private QueueConnection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            InitialContext initCtx = new InitialContext();
            QueueConnectionFactory connectionFactory =
                    (QueueConnectionFactory) initCtx.lookup("java:comp/env/jms/ConnectionFactory");
            connection = connectionFactory.createQueueConnection();
            QueueSession queueSession = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) initCtx.lookup("java:comp/env/jms/queue/MailQueue");
            QueueReceiver receiver = queueSession.createReceiver(queue);
            connection.start();
            log.info("Listen to JMS messages...");
            listenerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Message m = receiver.receive();
                        // TODO implement mail sending
                        if (m instanceof TextMessage) {
                            TextMessage tm = (TextMessage) m;
                            String text = tm.getText();
                            log.info("Received TextMessage with text '{}'", text);
                        }
                    }
                } catch (Exception e) {
                    log.error("Receiving messages failder: " + e.getMessage(), e);
                }
            });
            listenerThread.start();
        } catch (Exception e) {
            log.error("JMS failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                log.warn("Couldn't close JMSConnection: " + e.getMessage(), e);
            }
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }
}
