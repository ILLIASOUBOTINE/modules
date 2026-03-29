package org.example.dao;

import org.example.entity.User;

/**
 * DAO interface for {@link User} entity.
 * <p>
 * Extends the generic {@link CrudDao} interface to provide
 * basic CRUD operations and adds a method to find a user by email.
 */
public interface UserDao extends CrudDao<User> {

    /**
     * Finds a user by their unique email.
     *
     * @param email email of the user
     * @return {@link User} entity with the given email, or {@code null} if not found
     */
    User findByEmail(String email);
}