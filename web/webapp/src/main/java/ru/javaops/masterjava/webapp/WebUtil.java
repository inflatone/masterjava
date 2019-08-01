package ru.javaops.masterjava.webapp;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import ru.javaops.masterjava.util.Functions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class WebUtil {
    public static void doAndWriteResponse(HttpServletResponse response, Functions.SupplierEx<String> doer) throws IOException {
        log.info("Start sending");
        response.setCharacterEncoding(UTF_8.name());
        String result;
        try {
            log.info("Start processing");
            result = doer.get();
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            String message = e.getMessage();
            result = message != null ? message : e.getClass().getName();
        }
        response.getWriter().write(result);
    }

    public static String getNotEmptryParam(HttpServletRequest request, String param) {
        String value = request.getParameter(param);
        checkArgument(!Strings.isNullOrEmpty(value), param + " must not be empty");
        return value;
    }

    public static MailObject createMailObject(HttpServletRequest req) throws IOException, ServletException {
        Part filePart = req.getPart("attach");
        return new MailObject(
                getNotEmptryParam(req, "users"), req.getParameter("subject"), getNotEmptryParam(req, "body"),
                filePart == null ? ImmutableList.of() : ImmutableList.of(
                        new SimpleImmutableEntry<>(filePart.getSubmittedFileName(), filePart.getInputStream().readAllBytes()))
        );
    }
}
