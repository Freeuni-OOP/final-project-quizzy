package quizzy.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Singleton holder for the Hibernate {@link SessionFactory}.
 * Initializes from {@code hibernate.cfg.xml} on the classpath.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Failed to create SessionFactory: " + ex.getMessage());
        }
    }

    /**
     * Returns the shared SessionFactory instance.
     *
     * @return the singleton SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Closes the SessionFactory and releases all resources.
     * Should be called during application shutdown.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
