package ru.javaops.masterjava.persist.dao;

import org.junit.jupiter.api.extension.ExtendWith;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.DBITestProvider;
import ru.javaops.masterjava.persist.TimingExtension;

@ExtendWith(TimingExtension.class)
public abstract class AbstractDaoTest<DAO extends AbstractDao> {
    static {
        DBITestProvider.initDBI();
    }

    protected DAO dao;

    public AbstractDaoTest(Class<DAO> daoClass) {
        this.dao = DBIProvider.getDao(daoClass);
    }
}