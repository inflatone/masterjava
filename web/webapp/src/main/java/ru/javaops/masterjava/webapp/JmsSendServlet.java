package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.IllegalStateException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.javaops.masterjava.webapp.WebUtil.createMailObject;
import static ru.javaops.masterjava.webapp.WebUtil.doAndWriteResponse;

@Slf4j
@WebServlet("/sendJms")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 25)
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
        request.setCharacterEncoding(UTF_8.name());
        var mailObject = createMailObject(request);
        doAndWriteResponse(response, () -> sendJms(mailObject));
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
