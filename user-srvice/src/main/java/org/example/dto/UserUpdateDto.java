package org.example.dto;

/**
 * Data Transfer Object for updating user information.
 *
 * @param name the new name of the user
 * @param email the new email address of the user
 * @param age the age of the user
 */
public record UserUpdateDto(
        String name,
        String email,
        Integer age
) { }
