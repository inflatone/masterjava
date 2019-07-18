package ru.javaops.masterjava.upload;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.javaops.masterjava.persist.model.type.UserFlag;
import ru.javaops.masterjava.upload.PayloadProcessor.FailedEmails;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);

    private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

    /**
     * @return failed users chunks
     */
    public List<FailedEmails> process(final StaxStreamProcessor processor, Map<String, Group> groups, Map<String, City> cities, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize = " + chunkSize);
        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>(); // ordered map (emailRange -> chunk future)
        int id = userDao.getSeqAndSkip(chunkSize);
        List<User> chunk = new ArrayList<>(chunkSize);
        List<UserGroup> chunkUserGroups = new ArrayList<>(chunkSize);

        val unmarshaller = jaxbParser.createUnmarshaller();
        List<FailedEmails> failed = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            // unmarshal doesn't get refs
            val cityRef = processor.getAttribute("city");
            val groupRefs = processor.getAttribute("groupRefs");
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            String email = xmlUser.getEmail();
            if (cities.get(cityRef) == null) {
                failed.add(new FailedEmails(email, "City '" + cityRef + "' is not presented in DB"));
            } else {
                List<String> groupNames = groupRefs == null
                        ? Collections.emptyList()
                        : Splitter.on(' ').splitToList(groupRefs);
                if (!groups.keySet().containsAll(groupNames)) {
                    failed.add(new FailedEmails(email, "One of group from '" + groupRefs + "' is not present in DB"));
                } else {
                    final User user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);
                    chunk.add(user);
                    List<UserGroup> userGroups = StreamEx.of(groupNames).map(name -> new UserGroup(user.getId(), groups.get(name).getId())).toList();
                    chunkUserGroups.addAll(userGroups);
                    if (chunk.size() == chunkSize) {
                        addChunkFutures(chunkFutures, chunk, chunkUserGroups);
                        chunk = new ArrayList<>(chunkSize);
                        chunkUserGroups = new ArrayList<>(chunkSize);
                        id = userDao.getSeqAndSkip(chunkSize);
                    }
                }
            }
        }

        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk, chunkUserGroups);
        }

        List<String> allAlreadyPresents = new ArrayList<>();
        chunkFutures.forEach((emailRange, future) -> {
            try {
                List<String> alreadyPresentsInChunk = future.get();
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

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk, List<UserGroup> chunkUserGroups) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executor.submit(() -> {
            // https://www.programcreek.com/java-api-examples/index.php?api=org.skife.jdbi.v2.TransactionCallback
            List<String> alreadyPresentedEmails = DBIProvider.getDBI().inTransaction((conn, status) -> {
                UserDao tUserDao = conn.attach(UserDao.class);
                UserGroupDao tUserGroupDao = conn.attach(UserGroupDao.class);
                List<User> alreadyPresented = tUserDao.insertAndGetConflictEmails(chunk);
                Set<Integer> alreadyPresentedUserIds = StreamEx.of(alreadyPresented).map(User::getId).toSet();
                tUserGroupDao.insertBatch(
                        StreamEx.of(chunkUserGroups)
                                .filter(group -> !alreadyPresentedUserIds.contains(group.getUserId()))
                                .toList()
                );
                return StreamEx.of(alreadyPresented).map(User::getEmail).toList();
            });
            // let gc clear chunk after insert
            chunk.clear();
            chunkUserGroups.clear();
            return alreadyPresentedEmails;
        });
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }
}

