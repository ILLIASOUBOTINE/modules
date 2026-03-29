package org.example.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate configuration utility class.
 * <p>
 * Initializes a singleton {@link SessionFactory} using Hibernate's configuration file (hibernate.cfg.xml).
 * Provides a global access point to obtain Hibernate sessions.
 */
public class HibernateConfig {

    /**
     * Singleton {@link SessionFactory} instance for the application.
     */
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure() // loads hibernate.cfg.xml from classpath
                    .buildSessionFactory();
        } catch (Throwable e) {
            // Wrap any initialization error in an ExceptionInInitializerError
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Returns the singleton {@link SessionFactory} instance.
     *
     * @return the Hibernate {@link SessionFactory}
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}