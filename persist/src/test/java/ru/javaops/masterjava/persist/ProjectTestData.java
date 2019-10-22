package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableMap;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;

import java.util.Map;

public class ProjectTestData {
    public static Project TOPJAVA;
    public static Project MASTERJAVA;
    public static Map<String, Project> PROJECTS;

    public static int TOPJAVA_ID;
    public static int MASTERJAVA_ID;

    public static void init() {
        TOPJAVA = new Project("topjava", "Topjava");
        MASTERJAVA = new Project("masterjava", "Masterjava");
        PROJECTS = ImmutableMap.of(
                TOPJAVA.getName(), TOPJAVA,
                MASTERJAVA.getName(), MASTERJAVA
        );

    }

    public static void setUp() {
        var projectDao = DBIProvider.getDao(ProjectDao.class);
        projectDao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> PROJECTS.values().forEach(projectDao::insert));

        TOPJAVA_ID = TOPJAVA.getId();
        MASTERJAVA_ID = MASTERJAVA.getId();
    }
}
