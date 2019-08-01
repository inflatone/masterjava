package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.MailUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.javaops.masterjava.webapp.WebUtil.doAndWriteResponse;
import static ru.javaops.masterjava.webapp.WebUtil.getNotEmptryParam;

@Slf4j
@WebServlet("/sendSoap")
public class SoapSendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(UTF_8.name());
        doAndWriteResponse(resp, () -> {
            String users = getNotEmptryParam(req, "users");
            String subject = req.getParameter("subject");
            String body = getNotEmptryParam(req, "body");
            Part filePart = req.getPart("attach");
            GroupResult groupResult = MailWSClient.sendBulk(MailUtils.split(users), subject, body,
                    filePart == null ? null : ImmutableList.of(
                            MailUtils.getAttachment(filePart.getSubmittedFileName(), filePart.getInputStream())
                    ));
            return groupResult.toString();
        });
    }
}
