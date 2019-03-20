package ru.masterjava.persist.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                CityDaoTest.class,
                GroupDaoTest.class,
                ProjectDaoTest.class,
                UserDaoTest.class,
                UserGroupDaoTest.class
        }
)
public class AllDaoTest {
}
