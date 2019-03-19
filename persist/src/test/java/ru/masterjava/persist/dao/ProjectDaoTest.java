package ru.masterjava.persist.dao;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;
import ru.masterjava.persist.ProjectTestData;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static ru.masterjava.persist.ProjectTestData.PROJECTS;

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
        final Map<String, Project> projects = dao.getAsMap();
        assertEquals(PROJECTS, projects);
        System.out.println(projects.values());
    }

}
