package org.example.dao;

import org.example.entity.User;
import org.hibernate.Session;

/**
 * Implementation of {@link UserDao} using Hibernate.
 * <p>
 * Extends {@link CrudDaoImpl} to inherit generic CRUD operations
 * and provides a concrete implementation for {@link #findByEmail(String)}.
 */
public class UserDaoImpl extends CrudDaoImpl<User> implements UserDao {

    /**
     * Constructs a new {@link UserDaoImpl} and sets the entity class to {@link User}.
     */
    public UserDaoImpl() {
        this.eClass = User.class;
    }

    /**
     * Finds a {@link User} entity by its email.
     * <p>
     * Opens a new Hibernate session and executes a query to fetch a single result.
     *
     * @param email email of the user
     * @return {@link User} with the given email, or {@code null} if not found
     */
    @Override
    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }
}