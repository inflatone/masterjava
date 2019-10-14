package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JAXBParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProcessor {
    private static final JAXBParser jaxbParser = new JAXBParser(ObjectFactory.class);


    public List<User> process(final InputStream in) throws XMLStreamException, JAXBException {
        final var processor = new StaxStreamProcessor(in);
        final var unmarshaller = jaxbParser.createUnmarshaller();
        List<User> users = new ArrayList<>();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            final var user = new User(xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
            users.add(user);
        }
        return users;
    }
}
