package org.example.dao;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for integration tests using Testcontainers and Hibernate.
 * <p>
 * This class ensures that a single PostgreSQL container is shared across
 * all test classes extending it, significantly improving test performance.
 * It also handles the global {@link SessionFactory} initialization.
 * </p>
 */
public abstract class AbstractDaoTest {

    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("usertestdb")
                    .withUsername("test")
                    .withPassword("test");

    protected static SessionFactory sessionFactory;

    @BeforeAll
    static void startContainer() {
        if (!postgres.isRunning()) {
            postgres.start();
        }

        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
            configuration.setProperty("hibernate.connection.username", postgres.getUsername());
            configuration.setProperty("hibernate.connection.password", postgres.getPassword());
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

            configuration.addAnnotatedClass(org.example.entity.UserEntity.class);

            sessionFactory = configuration.buildSessionFactory();
        }
    }

    @AfterAll
    static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
