package org.example.service;

import org.example.dao.UserDao;
import org.example.dto.UserUpdateDto;
import org.example.entity.UserEntity;
import org.example.exception.DuplicateException;
import org.example.exception.DatabaseException;

import java.util.List;

/**
 * Service layer implementation for managing {@link UserEntity} entities.
 * <p>
 * Handles business logic and centrally wraps DAO-level exceptions (Hibernate/PostgreSQL)
 * into custom exceptions:
 * <ul>
 *     <li>{@link DuplicateException} — when trying to create or update a user with an existing email.</li>
 *     <li>{@link DatabaseException} — for other unexpected database errors.</li>
 * </ul>
 * <p>
 * This service uses {@link UserDao} for all CRUD operations and ensures safe
 * interaction with a console interface or other layers of the application.
 */
public class UserServiceImpl implements UserService {

    /**
     * DAO for accessing {@link UserEntity} entities.
     */
    private final UserDao dao;

    /**
     * Constructs the service with a given {@link UserDao}.
     *
     * @param dao DAO instance for user operations
     */
    public UserServiceImpl(UserDao dao) {
        this.dao = dao;
    }

    /**
     * Creates a new user.
     * <p>
     * Checks if a user with the given email already exists. If yes, throws {@link DuplicateException}.
     * Any other database errors are wrapped in {@link DatabaseException}.
     *
     * @param name  user name
     * @param email user email (must be unique)
     * @param age   user age
     * @throws DuplicateException if a user with the given email already exists
     * @throws DatabaseException  if a database error occurs
     */
    @Override
    public void createUser(String name, String email, int age) {
        try {
            if (dao.findByEmail(email) != null) {
                throw new DuplicateException("User with this email already exists");
            }
            UserEntity user = new UserEntity(name, email, age);
            dao.save(user);
        } catch (DuplicateException | DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DatabaseException("Unexpected error while creating user", ex);
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id user ID
     * @return {@link UserEntity} with the given ID
     * @throws DatabaseException if the user is not found or a database error occurs
     */
    @Override
    public UserEntity getUser(Long id) {
        try {
            UserEntity user = dao.getById(id);
            if (user == null) {
                throw new DatabaseException("User not found with id: " + id);
            }
            return user;
        } catch (DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DatabaseException("Unexpected error while fetching user", ex);
        }
    }

    /**
     * Retrieves all users.
     *
     * @return list of all {@link UserEntity} entities
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public List<UserEntity> getAllUsers() {
        try {
            return dao.getAll();
        } catch (DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DatabaseException("Unexpected error while fetching users", ex);
        }
    }

    /**
     * Updates an existing user.
     * <p>
     * Fields in {@link UserUpdateDto} that are null remain unchanged.
     * Checks for email uniqueness before updating; throws {@link DuplicateException} if conflict exists.
     *
     * @param id  ID of the user to update
     * @param dto DTO containing new field values
     * @throws DuplicateException if the new email is already used by another user
     * @throws DatabaseException  if the user is not found or a database error occurs
     */
    @Override
    public void updateUser(Long id, UserUpdateDto dto) {
        try {
            UserEntity user = dao.getById(id);
            if (user == null) {
                throw new DatabaseException("User not found with id: " + id);
            }

            if (dto.name() != null) user.setName(dto.name());

            if (dto.email() != null) {
                UserEntity exist = dao.findByEmail(dto.email());
                if (exist != null && !exist.getId().equals(user.getId())) {
                    throw new DuplicateException("User with this email already exists");
                }
                user.setEmail(dto.email());
            }

            if (dto.age() != null) user.setAge(dto.age());

            dao.update(user);

        } catch (DuplicateException ex) {
           throw new DuplicateException("Duplicate error: " + ex.getMessage(), ex);
        } catch (DatabaseException ex) {
            throw new DatabaseException("Database error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new DatabaseException("Unexpected error while updating user", ex);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id user ID
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public void deleteUser(Long id) {
        try {
            UserEntity user = dao.getById(id);
            if (user == null) {
                throw new DatabaseException("User not found with id: " + id);
            }
            dao.delete(id);
        } catch (DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DatabaseException("Unexpected error while deleting user", ex);
        }
    }
}