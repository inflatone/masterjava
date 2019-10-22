package ru.javaops.masterjava.persist.dao;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.persist.CityTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.masterjava.persist.CityTestData.CITIES;

class CityDaoTest extends AbstractDaoTest<CityDao> {
    CityDaoTest() {
        super(CityDao.class);
    }

    @BeforeAll
    static void init() {
        CityTestData.init();
    }

    @BeforeEach
    void setUp() {
        CityTestData.setUp();
    }

    @Test
    void getAll() {
        val cities = dao.getAsMap();
        assertEquals(CITIES, cities);
        System.out.println(cities.values());
    }
}
