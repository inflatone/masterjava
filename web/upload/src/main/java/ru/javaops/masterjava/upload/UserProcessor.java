package ru.javaops.masterjava.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserProcessor.class);
    private static final int NUMBER_THREADS = 4;

    private static final JAXBParser jaxbParser = new JAXBParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    public static class FailedChunk {
        public String emailOrRange;
        public String reason;

        FailedChunk(String emailOrRange, String reason) {
            this.emailOrRange = emailOrRange;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return emailOrRange + " : " + reason;
        }
    }

    /*
     * return failed users chunks
     */
    public List<FailedChunk> process(final InputStream in, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);
        return new Callable<List<FailedChunk>>() {
            class ChunkFuture {
                private String emailRange;
                private Future<List<String>> future;

                public ChunkFuture(List<User> userChunk, Future<List<String>> future) {
                    this.future = future;
                    this.emailRange = userChunk.get(0).getEmail();
                    if (userChunk.size() > 1) {
                        this.emailRange += '-' + userChunk.get(userChunk.size() - 1).getEmail();
                    }
                }
            }

            @Override
            public List<FailedChunk> call() throws XMLStreamException, JAXBException {
                List<ChunkFuture> futures = new ArrayList<>();
                int id = userDao.getSeqAndSkip(chunkSize);
                var userChunk = new ArrayList<User>(chunkSize);
                final var processor = new StaxStreamProcessor(in);
                final var unmarshaller = jaxbParser.createUnmarshaller();

                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    var xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
                    final var user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
                    userChunk.add(user);
                    if (userChunk.size() == chunkSize) {
                        futures.add(submit(userChunk));
                        userChunk = new ArrayList<>(chunkSize);
                        id = userDao.getSeqAndSkip(chunkSize);
                    }
                }

                if (!userChunk.isEmpty()) {
                    futures.add(submit(userChunk));
                }

                var failedChunks = new ArrayList<FailedChunk>();
                futures.forEach(chunk -> {
                    try {

                    } catch (Exception e) {
                        log.error(chunk.emailRange + " failed", e);
                        failedChunks.add(new FailedChunk(chunk.emailRange, e.toString()));
                    }
                });
                return failedChunks;
            }

            private ChunkFuture submit(List<User> userChunk) {
                var chunkFuture = new ChunkFuture(userChunk,
                        executorService.submit(() -> userDao.insertAndGetConflictEmails(userChunk))
                );
                log.info("Submit chunk: " + chunkFuture.emailRange);
                return chunkFuture;
            }
        }.call();
    }
}
