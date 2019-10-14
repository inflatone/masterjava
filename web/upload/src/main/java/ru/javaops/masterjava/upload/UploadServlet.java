package ru.javaops.masterjava.upload;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10) // 10 MB in memory limit
public class UploadServlet extends HttpServlet {
    private final UserProcessor userProcessor = new UserProcessor();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var webContext = new WebContext(request, response, request.getServletContext(), request.getLocale());
        engine.process("upload", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final var webContext = new WebContext(request, response, request.getServletContext(), request.getLocale());
        try {
            // http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            var filePart = request.getPart("fileToUpload");
            if (filePart.getSize() == 0) {
                throw new IllegalStateException("Upload file have not been selected");
            }
            try (var in = filePart.getInputStream()) {
                var users = userProcessor.process(in);
                webContext.setVariable("users", users);
                engine.process("result", webContext, response.getWriter());
            }

        } catch (XMLStreamException e) {
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, response.getWriter());
        }
    }
}
