package ru.javaops.masterjava.upload;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("/")
public class UploadServlet extends HttpServlet {
    private final UserProcessor userProcessor = new UserProcessor();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var webContext = new WebContext(request, response, request.getServletContext(), request.getLocale());
        engine.process("upload", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var uploader = new ServletFileUpload();
        final var webContext = new WebContext(request, response, request.getServletContext(), request.getLocale());

        try {
            // https://commons.apache.org/proper/commons-fileupload/streaming.html
            final var itemIterator = uploader.getItemIterator(request);
            while (itemIterator.hasNext()) {
                var fileItemStream = itemIterator.next();
                if (!fileItemStream.isFormField()) {
                    try (var in = fileItemStream.openStream()) {
                        var users = userProcessor.process(in);
                        webContext.setVariable("users", users);
                        engine.process("result", webContext, response.getWriter());
                    }
                    break; // expect that it's only one file
                }
            }
        } catch (FileUploadException | XMLStreamException e) {
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, response.getWriter());
        }
    }
}
