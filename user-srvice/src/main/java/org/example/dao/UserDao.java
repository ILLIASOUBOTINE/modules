package org.example.dao;

import org.example.entity.UserEntity;

/**
 * DAO interface for {@link UserEntity} entity.
 * <p>
 * Extends the generic {@link CrudDao} interface to provide
 * basic CRUD operations and adds a method to find a user by email.
 */
public interface UserDao extends CrudDao<UserEntity> {

    /**
     * Finds a user by their unique email.
     *
     * @param email email of the user
     * @return {@link UserEntity} entity with the given email, or {@code null} if not found
     */
    UserEntity findByEmail(String email);
}