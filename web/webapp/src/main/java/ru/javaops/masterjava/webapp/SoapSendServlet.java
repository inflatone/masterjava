package ru.javaops.masterjava.webapp;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.MailUtils;
import ru.javaops.masterjava.web.WebStateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@WebServlet("/sendSoap")
@MultipartConfig
public class SoapSendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String result;
        try {
            log.info("Start sending");
            request.setCharacterEncoding(UTF_8.name());
            response.setCharacterEncoding(UTF_8.name());
            result = getParametersAndSend(request);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        response.getWriter().write(result);
    }

    private String getParametersAndSend(HttpServletRequest request) throws WebStateException, IOException, ServletException {
        var users = request.getParameter("users");
        var subject = request.getParameter("subject");
        var body = request.getParameter("body");
        var filePart = request.getPart("attach");
        var groupResult = MailWSClient.sendBulk(MailUtils.split(users), subject, body,
                filePart == null ? null : List.of(MailUtils.getAttachment(filePart.getSubmittedFileName(), filePart.getInputStream())));
        var result = groupResult.toString();
        log.info("Processing finished with result: {}", result);
        return result;
    }
}
