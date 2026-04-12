package org.example.repository;

import org.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link UserEntity} providing standard CRUD operations.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Checks if a user with the given email address already exists in the database.
     *
     * @param email the email address to search for.
     * @return true if the email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
