package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.GroupTestData;

import static org.junit.Assert.assertEquals;
import static ru.javaops.masterjava.persist.GroupTestData.*;

public class GroupDaoTest extends AbstractDaoTest<GroupDao> {
    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeClass
    public static void init() {
        GroupTestData.init();
    }

    @Before
    public void setUp() {
        GroupTestData.setUp();
    }

    @Test
    public void getAll() {
        val groups = dao.getAsMap();
        assertEquals(GROUPS, groups);
        System.out.println(groups.values());
    }
}
