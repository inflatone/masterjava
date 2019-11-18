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
import static ru.javaops.masterjava.webapp.WebUtil.doAndWriteResponse;
import static ru.javaops.masterjava.webapp.WebUtil.getNotEmptyParameter;

@Slf4j
@WebServlet("/sendSoap")
@MultipartConfig
public class SoapSendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(UTF_8.name());
        doAndWriteResponse(response, () -> getParametersAndSend(request));
    }

    private String getParametersAndSend(HttpServletRequest request) throws WebStateException, IOException, ServletException {
        var users = getNotEmptyParameter(request, "users");
        var subject = request.getParameter("subject");
        var body = getNotEmptyParameter(request, "body");
        var filePart = request.getPart("attach");
        var groupResult = MailWSClient.sendBulk(MailUtils.split(users), subject, body,
                filePart == null ? null : List.of(MailUtils.getAttachment(filePart.getSubmittedFileName(), filePart.getInputStream())));
        return groupResult.toString();
    }
}
