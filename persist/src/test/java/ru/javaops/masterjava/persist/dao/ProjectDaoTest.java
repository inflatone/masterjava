package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.ProjectTestData;
import ru.masterjava.persist.dao.ProjectDao;

import static org.junit.Assert.assertEquals;
import static ru.javaops.masterjava.persist.ProjectTestData.*;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {
    public ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @BeforeClass
    public static void init() {
        ProjectTestData.init();
    }

    @Before
    public void setUp() {
        ProjectTestData.setUp();
    }

    @Test
    public void getAll() {
        val projects = dao.getAsMap();
        assertEquals(PROJECTS, projects);
        System.out.println(projects);
    }
}
