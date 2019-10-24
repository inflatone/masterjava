package ru.javaops.masterjava.service.mail.persist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.persist.dao.AbstractDaoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaops.masterjava.service.mail.persist.MaiLCaseTestData.DATE_FROM;
import static ru.javaops.masterjava.service.mail.persist.MaiLCaseTestData.MAIL_CASES;

class MailCaseDaoTest extends AbstractDaoTest<MailCaseDao> {
    MailCaseDaoTest() {
        super(MailCaseDao.class);
    }

    @BeforeEach
    void setUp() {
        MaiLCaseTestData.setUp();
    }

    @Test
    void getAll() {
        assertEquals(MAIL_CASES, dao.getAfter(DATE_FROM));
    }
}
