package ru.javaops.masterjava.service.mail.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.Attachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;
import java.util.Set;

public class MailUtils {

    public static Set<Addressee> split(String addressees) {
        Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(addressees);
        return ImmutableSet.copyOf(Iterables.transform(split, Addressee::new));
    }

    @Data
    @AllArgsConstructor
    public static class MailObject implements Serializable {
        @NotNull
        private String users;
        private String subject;
        @NotNull
        private String body;
        private String attachName;
        private byte[] attachData;
    }

    public static MailObject getMailObject(String users, String subject, String body, String name, InputStream in) {
        try {
            return new MailObject(users, subject, body, name, in == null ? null : IOUtils.toByteArray(in));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Attachment getAttachment(String name, byte[] attachData) {
        return new Attachment(name, new DataHandler((ProxyDataSource) () -> new ByteArrayInputStream(attachData)));
    }

    public static Attachment getAttachment(String name, InputStream in) {
        //    http://stackoverflow.com/questions/2830561/how-to-convert-an-inputstream-to-a-datahandler
        //    http://stackoverflow.com/a/10783565/548473
        return new Attachment(name, new DataHandler((ProxyDataSource) () -> new CloseShieldInputStream(in)));
    }

    public interface ProxyDataSource extends DataSource {
        @Override
        default OutputStream getOutputStream() throws IOException {
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
