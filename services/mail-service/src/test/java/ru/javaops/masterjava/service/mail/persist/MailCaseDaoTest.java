package ru.javaops.masterjava.service.mail.persist;

import org.junit.Before;
import org.junit.Test;
import ru.masterjava.persist.dao.AbstractDaoTest;

import static org.junit.Assert.assertEquals;

public class MailCaseDaoTest extends AbstractDaoTest<MailCaseDao> {
    public MailCaseDaoTest() {
        super(MailCaseDao.class);
    }

    @Before
    public void setUp() {
        MailCaseTestData.setUp();
    }

    @Test
    public void getAll() {
        assertEquals(MailCaseTestData.MAIL_CASES, dao.getAfter(MailCaseTestData.DATE_FROM));
    }
}
