package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
            initCtx();
        } catch (Exception e) {
            throw new IllegalStateException("JMS init failed", e);
        }
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
        super.destroy();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result;
        try {
            log.info("Start sending");
            request.setCharacterEncoding(UTF_8.name());
            response.setCharacterEncoding(UTF_8.name());
            var filePart = request.getPart("attach");
            var mailObject = new MailObject(
                    request.getParameter("users"),
                    request.getParameter("subject"),
                    request.getParameter("body"),
                    filePart == null ? List.of()
                            : List.of(new SimpleImmutableEntry<>(filePart.getSubmittedFileName(), IOUtils.toByteArray(filePart.getInputStream())))
            );
            result = sendJms(mailObject);
            log.info("Processing finished with result: " + result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        response.getWriter().write(result);
    }

    private void initCtx() throws NamingException, JMSException {
        var initCtx = new InitialContext();
        var connectionFactory = (ConnectionFactory) initCtx.lookup("java:comp/env/jms/ConnectionFactory");
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer((Destination) initCtx.lookup("java:comp/env/jms/queue/MailQueue"));
    }

    private synchronized String sendJms(MailObject mailObject) throws JMSException {
        var objMessage = session.createObjectMessage();
        objMessage.setObject(mailObject);
        producer.send(objMessage);
        return "Successfully send JMS message";
    }
}
