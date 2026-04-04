package org.example.dao;

import org.example.entity.UserEntity;
import org.hibernate.Session;

/**
 * Implementation of {@link UserDao} using Hibernate.
 * <p>
 * Extends {@link CrudDaoImpl} to inherit generic CRUD operations
 * and provides a concrete implementation for {@link #findByEmail(String)}.
 */
public class UserDaoImpl extends CrudDaoImpl<UserEntity> implements UserDao {

    /**
     * Constructs a new {@link UserDaoImpl} and sets the entity class to {@link UserEntity}.
     */
    public UserDaoImpl() {
        this.eClass = UserEntity.class;
    }

    /**
     * Finds a {@link UserEntity} entity by its email.
     * <p>
     * Opens a new Hibernate session and executes a query to fetch a single result.
     *
     * @param email email of the user
     * @return {@link UserEntity} with the given email, or {@code null} if not found
     */
    @Override
    public UserEntity findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM UserEntity WHERE email = :email", UserEntity.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }
}