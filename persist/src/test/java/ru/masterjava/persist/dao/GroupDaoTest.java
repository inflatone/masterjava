package ru.masterjava.persist.dao;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.masterjava.persist.GroupTestData;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static ru.masterjava.persist.GroupTestData.GROUPS;

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
        final Map<String, Group> groups = dao.getAsMap();
        assertEquals(GROUPS, groups);
        System.out.println(groups.values());
    }
}
