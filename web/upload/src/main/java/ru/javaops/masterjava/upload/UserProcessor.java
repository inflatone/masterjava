package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.model.UserFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProcessor {
    public List<User> process(final InputStream in) throws XMLStreamException {
        final var processor = new StaxStreamProcessor(in);
        List<User> users = new ArrayList<>();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final var email = processor.getAttribute("email");
            final var flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final var fullName = processor.getText();
            final var user = new User(fullName, email, flag);
            users.add(user);
        }
        return users;
    }
}
