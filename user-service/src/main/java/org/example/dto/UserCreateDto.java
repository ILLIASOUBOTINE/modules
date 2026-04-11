package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for creating a new user.
 * * @param name  The name of the user. Must not be blank.
 * @param email The email address of the user. Must be a valid email format and not blank.
 * @param age   The age of the user. Must be a positive number.
 */
public record UserCreateDto(
        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @Email(message = "Некорректный формат email")
        @NotBlank(message = "Email обязателен")
        String email,

        @Positive(message = "Возраст должен быть больше 0")
        int age
) {}
