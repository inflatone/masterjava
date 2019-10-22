package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.persist.ProjectTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.masterjava.persist.ProjectTestData.PROJECTS;

class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {
    ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @BeforeAll
    static void init() {
        ProjectTestData.init();
    }

    @BeforeEach
    void setUp() {
        ProjectTestData.setUp();
    }

    @Test
    void getAll() {
        val projects = dao.getAsMap();
        assertEquals(PROJECTS, projects);
        System.out.println(projects.values());
    }
}
