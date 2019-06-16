package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.model.Person;
import ru.javaops.masterjava.model.PersonFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PersonProcessor {
    public List<Person> process(final InputStream in) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(in);
        List<Person> persons = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final PersonFlag flag = PersonFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final Person person = new Person(fullName, email, flag);
            persons.add(person);
        }
        return persons;
    }
}
