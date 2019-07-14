package ru.javaops.masterjava.persist.dao;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.UserTestData;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.javaops.masterjava.persist.UserTestData.FIRST_5_USERS;

public class UserDaoTest extends AbstractDaoTest<UserDao> {
    public UserDaoTest() {
        super(UserDao.class);
    }

    @BeforeClass
    public static void init() {
        UserTestData.init();
    }

    @Before
    public void setUp() {
        UserTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        List<User> users = dao.getWithLimit(5);
        assertEquals(FIRST_5_USERS, users);
    }

    @Test
    public void insertBatch() {
        dao.clean();
        dao.insertBatch(FIRST_5_USERS, 3);
        assertEquals(5, dao.getWithLimit(100).size());
    }

    @Test
    public void getSeqAndSkipStep() {
        int firstSeq = dao.getSeqAndSkip(5);
        int secondSeq = dao.getSeqAndSkip(1);
        assertEquals(5, secondSeq - firstSeq);
    }
}
