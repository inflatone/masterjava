package ru.javaops.masterjava.upload;

import lombok.AllArgsConstructor;
import lombok.val;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;

public class PayloadProcessor {
    private final UserProcessor userProcessor = new UserProcessor();

    @AllArgsConstructor
    public static class FailedEmails {
        String emailsOrRange;
        String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }

    public List<FailedEmails> process(InputStream in, int chunkSize) throws XMLStreamException, JAXBException {
        val processor = new StaxStreamProcessor(in);
        return userProcessor.process(processor, chunkSize);
    }
}
