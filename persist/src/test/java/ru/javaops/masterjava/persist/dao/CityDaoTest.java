package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.CityTestData;

import static org.junit.Assert.assertEquals;
import static ru.javaops.masterjava.persist.CityTestData.CITIES;

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
        val cities = dao.getAsMap();
        assertEquals(CITIES, cities);
        System.out.println(cities.values());
    }
}
