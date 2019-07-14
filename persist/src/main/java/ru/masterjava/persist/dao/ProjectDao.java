package ru.masterjava.persist.dao;

import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import ru.masterjava.persist.model.Project;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class ProjectDao implements AbstractDao {
    @Override
    @SqlUpdate("TRUNCATE project CASCADE")
    public abstract void clean();

    @SqlQuery("SELECT * FROM project ORDER BY name")
    public abstract List<Project> getAll();

    public Map<String, Project> getAsMap() {
        return StreamEx.of(getAll()).toMap(Project::getName, Function.identity());
    }

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO project (name, description) VALUES (:name, :description)")
    public abstract int insertGeneratedId(@BindBean Project project);

    public void insert(@BindBean Project project) {
        int id = insertGeneratedId(project);
        project.setId(id);
    }
}
