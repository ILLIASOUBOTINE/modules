package org.example.dto;

public record UserEvent(
        String email,
        OperationType operationType
) {}
