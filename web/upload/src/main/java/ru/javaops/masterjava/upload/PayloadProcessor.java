package ru.javaops.masterjava.upload;

import com.google.common.base.MoreObjects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;

public class PayloadProcessor {
    public static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);

    private final ProjectGroupProcessor projectGroupProcessor = new ProjectGroupProcessor();
    private final CityProcessor cityProcessor = new CityProcessor();
    private final UserProcessor userProcessor = new UserProcessor();

    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class FailedEmails {
        String emailsOrRange;
        String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }

    public List<FailedEmails> process(InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        val groups = projectGroupProcessor.process(processor);
        val cities = cityProcessor.process(processor);
        return userProcessor.process(processor, groups, cities, chunkSize);
    }
}
