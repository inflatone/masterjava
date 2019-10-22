package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.persist.GroupTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.masterjava.persist.GroupTestData.GROUPS;

class GroupDaoTest extends AbstractDaoTest<GroupDao> {
    GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeAll
    static void init() {
        GroupTestData.init();
    }

    @BeforeEach
    void setUp() {
        GroupTestData.setUp();
    }

    @Test
    void getAll() {
        val groups = dao.getAsMap();
        assertEquals(GROUPS, groups);
        System.out.println(groups.values());
    }
}
