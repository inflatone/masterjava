package ru.javaops.masterjava.service.mail.util;

import lombok.AllArgsConstructor;
import org.apache.commons.io.input.CloseShieldInputStream;
import ru.javaops.masterjava.service.mail.Attachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Attachments {
    public static Attachment getAttachment(String name, InputStream in) {
        return new Attachment(name, new DataHandler(new InputStreamDataSource(in)));
    }

    // http://stackoverflow.com/questions/2830561/how-to-convert-an-inputstream-to-a-datahandler
    @AllArgsConstructor
    private static class InputStreamDataSource implements ProxyDataSource {
        private InputStream in;

        @Override
        public InputStream getInputStream() {
            return new CloseShieldInputStream(in);
        }

    }

    @FunctionalInterface
    public interface ProxyDataSource extends DataSource {
        @Override
        InputStream getInputStream() throws IOException;

        @Override
        default OutputStream getOutputStream() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        default String getContentType() {
            return "application/octet-stream";
        }

        @Override
        default String getName() {
            return "";
        }
    }
}