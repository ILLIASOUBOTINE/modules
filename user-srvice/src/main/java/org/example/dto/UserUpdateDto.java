package org.example.dto;

public record UserUpdateDto(
        String name,
        String email,
        Integer age
) { }
