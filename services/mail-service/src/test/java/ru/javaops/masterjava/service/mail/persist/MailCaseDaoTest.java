package ru.javaops.masterjava.service.mail.persist;

import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.persist.dao.AbstractDaoTest;

import static org.junit.Assert.assertEquals;
import static ru.javaops.masterjava.service.mail.persist.MailCaseTestData.DATE_FROM;
import static ru.javaops.masterjava.service.mail.persist.MailCaseTestData.MAIL_CASES;

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
        assertEquals(MAIL_CASES, dao.getAfter(DATE_FROM));
    }
}
