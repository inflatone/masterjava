package ru.javaops.masterjava.persist;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;
import org.skife.jdbi.v2.tweak.ConnectionFactory;
import org.slf4j.Logger;
import ru.javaops.masterjava.persist.dao.AbstractDao;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import static org.slf4j.LoggerFactory.getLogger;

public class DBIProvider {
    private static final Logger log = getLogger(DBIProvider.class);

    private volatile static ConnectionFactory connectionFactory = null;

    private static class DBIHolder {
        static final DBI jDBI;

        static {
            final DBI dbi;
            if (connectionFactory != null) {
                log.info("Init jDBI with connectionFactory");
                dbi = new DBI(connectionFactory);
            } else {
                try {
                    log.info("Init jDBI with JNDI");
                    var context = new InitialContext();
                    dbi = new DBI((DataSource) context.lookup("java:/comp/env/jdbc/masterjava"));
                } catch (Exception e) {
                    throw new IllegalStateException("PostgreSQL initialization failed", e);
                }
            }
            jDBI = dbi;
            jDBI.setSQLLog(new SLF4JLog());
        }
    }

    public static void init(ConnectionFactory connectionFactory) {
        DBIProvider.connectionFactory = connectionFactory;
    }

    public static DBI getDBI() {
        return DBIHolder.jDBI;
    }

    public static <T extends AbstractDao> T getDao(Class<T> daoClass) {
        return DBIHolder.jDBI.onDemand(daoClass);
    }
}
