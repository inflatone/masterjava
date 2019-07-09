package ru.javaops.masterjava.persist.dao;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.UserTestData;
import ru.masterjava.persist.dao.UserDao;
import ru.masterjava.persist.model.User;

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
}
