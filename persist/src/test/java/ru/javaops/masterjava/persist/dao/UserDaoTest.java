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

    public UserDaoTest() {
        super(UserDao.class);
    }

    @BeforeAll
    static void init() throws Exception {
        UserTestData.init();
    }

    @BeforeEach
    void setUp() throws Exception {
        UserTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        List<User> users = dao.getWithLimit(5);
        assertEquals(FIST5_USERS, users);
    }
}