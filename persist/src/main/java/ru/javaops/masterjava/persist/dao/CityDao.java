package ru.javaops.masterjava.persist.dao;

import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import ru.javaops.masterjava.persist.model.City;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class CityDao implements AbstractDao {
    @Override
    @SqlUpdate("TRUNCATE city CASCADE")
    public abstract void clean();

    @SqlQuery("SELECT * FROM city")
    public abstract List<City> getAll();

    public Map<String, City> getAsMap() {
        return StreamEx.of(getAll()).toMap(City::getRef, Function.identity());
    }

    @SqlUpdate("INSERT INTO city (ref, name) VALUES (:ref, :name)")
    public abstract void insert(@BindBean City city);

    @SqlBatch("INSERT INTO city (ref, name) VALUES (:ref, :name)")
    public abstract void insertBatch(@BindBean Collection<City> cities);
}
