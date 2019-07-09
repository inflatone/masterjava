package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.masterjava.persist.DBIProvider;
import ru.masterjava.persist.dao.UserDao;
import ru.masterjava.persist.model.User;
import ru.masterjava.persist.model.UserFlag;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);

    public List<User> process(final InputStream in, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(in);
        List<User> users = new ArrayList<>();
        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            ru.javaops.masterjava.xml.schema.User user = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            final User person = new User(user.getValue(), user.getEmail(), UserFlag.valueOf(user.getFlag().value()));
            users.add(person);
        }
        userDao.insertBatch(users, chunkSize);
        return users;
    }
}
