package ru.javaops.masterjava.persist.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.persist.UserTestData;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.masterjava.persist.UserTestData.FIST5_USERS;

class UserDaoTest extends AbstractDaoTest<UserDao> {

    UserDaoTest() {
        super(UserDao.class);
    }

    @BeforeAll
    static void init() {
        UserTestData.init();
    }

    @BeforeEach
    void setUp() {
        UserTestData.setUp();
    }

    @Test
    void getWithLimit() {
        List<User> users = dao.getWithLimit(5);
        assertEquals(FIST5_USERS, users);
    }

    @Test
    void insertBatch() {
        dao.clean();
        dao.insertBatch(FIST5_USERS, 3);
        assertEquals(5, dao.getWithLimit(100).size());
    }

    @Test
    void getSeqAndSkip() {
        int firstSeq = dao.getSeqAndSkip(5);
        int secondSeq = dao.getSeqAndSkip(1);
        assertEquals(5, secondSeq - firstSeq);
    }

}