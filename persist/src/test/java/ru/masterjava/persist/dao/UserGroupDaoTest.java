package ru.masterjava.persist.dao;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.masterjava.persist.UserGroupTestData;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static ru.masterjava.persist.GroupTestData.MASTERJAVA_01_ID;
import static ru.masterjava.persist.GroupTestData.TOPJAVA_07_ID;
import static ru.masterjava.persist.UserGroupTestData.getByGroupId;

public class UserGroupDaoTest extends AbstractDaoTest<UserGroupDao> {
    public UserGroupDaoTest() {
        super(UserGroupDao.class);
    }

    @BeforeClass
    public static void init() {
        UserGroupTestData.init();
    }

    @Before
    public void setUp() {
        UserGroupTestData.setUp();
    }

    @Test
    public void getAll() {
        Set<Integer> userIds = dao.getUserIds(MASTERJAVA_01_ID);
        assertEquals(getByGroupId(MASTERJAVA_01_ID), userIds);

        userIds = dao.getUserIds(TOPJAVA_07_ID);
        assertEquals(getByGroupId(TOPJAVA_07_ID), userIds);
    }
}
