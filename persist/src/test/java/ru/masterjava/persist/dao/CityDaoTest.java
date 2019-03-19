package ru.masterjava.persist.dao;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.masterjava.persist.CityTestData;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static ru.masterjava.persist.CityTestData.CITIES;

public class CityDaoTest extends AbstractDaoTest<CityDao> {
    public CityDaoTest() {
        super(CityDao.class);
    }

    @BeforeClass
    public static void init() {
        CityTestData.init();
    }

    @Before
    public void setUp() {
        CityTestData.setUp();
    }

    @Test
    public void getAll() {
        final Map<String, City> cities = dao.getAsMap();
        assertEquals(CITIES, cities);
        System.out.println(cities.values());
    }




}
