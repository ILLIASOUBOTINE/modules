package org.example.service;

import org.example.dto.UserCreateDto;
import org.example.dto.UserDTO;
import org.example.dto.UserUpdateDto;

import java.util.List;

/**
 * Service interface defining business logic for user management.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * @param dto user creation data.
     * @return the created user as a {@link UserDTO}.
     */
    UserDTO createUser(UserCreateDto dto);

    /**
     * Updates profile data for an existing user.
     * @param id the unique identifier of the user.
     * @param dto updated user data.
     * @return the updated user as a {@link UserDTO}.
     */
    UserDTO updateUser(Long id, UserUpdateDto dto);

    /**
     * Finds a user by their unique ID.
     * @param id the unique identifier.
     * @return the found user.
     */
    UserDTO getUserById(Long id);

    /**
     * Retrieves all users currently stored in the system.
     * @return list of user data transfer objects.
     */
    List<UserDTO> getAllUser();

    /**
     * Removes a user from the database.
     * @param id the unique identifier of the user to delete.
     */
    void deleteUser(Long id);
}
