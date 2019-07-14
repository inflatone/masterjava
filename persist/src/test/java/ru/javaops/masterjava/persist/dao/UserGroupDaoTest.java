package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.UserGroupTestData;

import static org.junit.Assert.assertEquals;
import static ru.javaops.masterjava.persist.GroupTestData.MASTERJAVA_01_ID;
import static ru.javaops.masterjava.persist.GroupTestData.TOPJAVA_07_ID;
import static ru.javaops.masterjava.persist.UserGroupTestData.getByGroupId;

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
        val userIds = dao.getUserIds(MASTERJAVA_01_ID);
        assertEquals(getByGroupId(MASTERJAVA_01_ID), userIds);

        val userIds1 = dao.getUserIds(TOPJAVA_07_ID);
        assertEquals(getByGroupId(TOPJAVA_07_ID), userIds1);
    }

}
