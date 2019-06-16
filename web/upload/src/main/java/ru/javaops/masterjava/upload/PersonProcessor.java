package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.model.Person;
import ru.javaops.masterjava.model.PersonFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PersonProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);

    public List<Person> process(final InputStream in) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(in);
        List<Person> persons = new ArrayList<>();
        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            User user = unmarshaller.unmarshal(processor.getReader(), User.class);
            final Person person = new Person(user.getValue(), user.getEmail(), PersonFlag.valueOf(user.getFlag().value()));
            persons.add(person);
        }
        return persons;
    }
}
