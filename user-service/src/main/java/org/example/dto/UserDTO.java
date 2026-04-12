package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object representing a user's full profile information.
 * Typically used for returning user data in response bodies.
 *
 * @param id    The unique database identifier.
 * @param name  The user's display name.
 * @param email The user's unique email address.
 * @param age   The user's age.
 */
public record UserDTO(
        Long id,

        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @Email(message = "Некорректный формат email")
        @NotBlank(message = "Email обязателен")
        String email,

        @Positive(message = "Возраст должен быть больше 0")
        int age
) {}