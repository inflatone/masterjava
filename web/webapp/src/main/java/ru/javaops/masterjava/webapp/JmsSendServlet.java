package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;

@Slf4j
@WebServlet("/sendJms")
public class JmsSendServlet extends HttpServlet {
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup("java:comp/env/jms/ConnectionFactory");
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer((Destination) context.lookup("java:comp/env/jms/queue/MailQueue"));
        } catch (Exception e) {
            throw new IllegalStateException("JMS init failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String result;
        try {
            log.info("Start sending");
            req.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Part filePart = req.getPart("attach");

            MailObject mailObject = new MailObject(
                    req.getParameter("users"), req.getParameter("subject"), req.getParameter("body"),
                    filePart == null ? ImmutableList.of() : ImmutableList.of(
                            new SimpleImmutableEntry<>(filePart.getSubmittedFileName(), filePart.getInputStream().readAllBytes())
                    )
            );
            result = sendJms(mailObject);
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        resp.getWriter().write(result);
    }

    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                log.warn("Couldn't close JMSConnection: " + e.getMessage(), e);
            }
        }
    }

    private synchronized String sendJms(MailObject mailObject) throws JMSException {
        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(mailObject);
        producer.send(objectMessage);
        return "Successfully send JMS message";
    }
}
