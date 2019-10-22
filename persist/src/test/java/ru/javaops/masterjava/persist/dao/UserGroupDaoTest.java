package ru.javaops.masterjava.persist.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.persist.UserGroupTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.masterjava.persist.GroupTestData.MASTERJAVA_01_ID;
import static ru.javaops.masterjava.persist.GroupTestData.TOPJAVA_07_ID;
import static ru.javaops.masterjava.persist.UserGroupTestData.getByGroupId;

class UserGroupDaoTest extends AbstractDaoTest<UserGroupDao> {
    UserGroupDaoTest() {
        super(UserGroupDao.class);
    }

    @BeforeAll
    static void init() {
        UserGroupTestData.init();
    }

    @BeforeEach
    void setUp() {
        UserGroupTestData.setUp();
    }

    @Test
    void getAll() {
        var userIds = dao.getUserIds(MASTERJAVA_01_ID);
        assertEquals(getByGroupId(MASTERJAVA_01_ID), userIds);

        userIds = dao.getUserIds(TOPJAVA_07_ID);
        assertEquals(getByGroupId(TOPJAVA_07_ID), userIds);
    }
}
