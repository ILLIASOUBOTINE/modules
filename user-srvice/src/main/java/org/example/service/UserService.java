package org.example.service;

import org.example.dto.UserUpdateDto;
import org.example.entity.UserEntity;
import org.example.exception.DuplicateException;

import java.util.List;

/**
 * Service layer for managing User entities.
 * Provides business logic and validation.
 */
public interface UserService {

    /**
     * Creates a new user.
     *
     * @param name user name
     * @param email unique email
     * @param age user age
     * @throws DuplicateException if email already exists
     */
    void createUser(String name, String email, int age);

    /**
     * Returns user by id.
     *
     * @param id user id
     * @return User or null if not found
     */
    UserEntity getUser(Long id);

    /**
     * Returns all users.
     *
     * @return list of users
     */
    List<UserEntity> getAllUsers();

    /**
     * Updates user fields.
     *
     * @param userId user id
     * @param dto fields to update
     */
    void updateUser(Long userId, UserUpdateDto dto);

    /**
     * Deletes user by id.
     *
     * @param id user id
     */
    void deleteUser(Long id);
}
