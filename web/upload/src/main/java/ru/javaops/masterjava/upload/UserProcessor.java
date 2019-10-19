package ru.javaops.masterjava.upload;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JAXBParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JAXBParser jaxbParser = new JAXBParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    @AllArgsConstructor
    public static class FailedEmails {
        String emailsOrRange;
        String reason;

        @Override
        public String toString() {
            return emailsOrRange + " : " + reason;
        }
    }

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final InputStream in, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        var chunkFutures = new LinkedHashMap<String, Future<List<String>>>(); // ordered map (emailRange -> chunk future)
        int id = userDao.getSeqAndSkip(chunkSize);
        var userChunk = new ArrayList<User>(chunkSize);
        val processor = new StaxStreamProcessor(in);
        val unmarshaller = jaxbParser.createUnmarshaller();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            var xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            final var user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
            userChunk.add(user);
            if (userChunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, userChunk);
                userChunk = new ArrayList<>(chunkSize);
                id = userDao.getSeqAndSkip(chunkSize);
            }
        }

        if (!userChunk.isEmpty()) {
            addChunkFutures(chunkFutures, userChunk);
        }

        var failed = new ArrayList<FailedEmails>();
        var allAlreadyPresents = new ArrayList<String>();
        chunkFutures.forEach((emailRange, task) -> {
            try {
                List<String> alreadyPresentsInChunk = task.get();
                log.info("{} successfully executed with already presents: {}", emailRange, alreadyPresentsInChunk);
                allAlreadyPresents.addAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));
            }
        });
        if (!allAlreadyPresents.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
        }
        return failed;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> userChunk) {
        var emailRange = String.format("[%s-%s]", userChunk.get(0).getEmail(), userChunk.get(userChunk.size() - 1).getEmail());
        var submittedTask = executorService.submit(() -> userDao.insertAndGetConflictEmails(userChunk));
        chunkFutures.put(emailRange, submittedTask);
        log.info("Submit chunk: " + emailRange);
    }
}
