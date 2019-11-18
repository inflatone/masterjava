package ru.javaops.masterjava.webapp;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import ru.javaops.masterjava.util.Functions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;

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
            var message = e.getMessage();
            result = message != null ? message : e.getClass().getName();
        }
        response.getWriter().write(result);
    }

    public static String getNotEmptyParameter(HttpServletRequest request, String key) {
        var value = request.getParameter(key);
        checkArgument(!Strings.isNullOrEmpty(value), key + " must not be empty");
        return value;
    }

    public static MailObject createMailObject(HttpServletRequest request) throws IOException, ServletException {
        var filePart = request.getPart("attach");
        return new MailObject(
                getNotEmptyParameter(request, "users"),
                request.getParameter("subject"),
                getNotEmptyParameter(request, "body"),
                filePart == null ? List.of()
                        : List.of(new AbstractMap.SimpleImmutableEntry<>(filePart.getSubmittedFileName(), IOUtils.toByteArray(filePart.getInputStream())))
        );
    }
}
