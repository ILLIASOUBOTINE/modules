package org.example.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response structure for API exceptions.
 * Used to provide clear feedback to the client when validation or business logic fails.
 *
 * @param timestamp The exact time when the error occurred.
 * @param status    The HTTP status code (e.g., 400, 404, 409).
 * @param message   A general description of the error.
 * @param errors    A map of specific field errors (e.g., "email" -> "Invalid format").
 */
public record ErrorResponse (
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, String> errors
) {}
