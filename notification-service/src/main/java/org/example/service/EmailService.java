package org.example.service;

import org.example.dto.OperationType;

/**
 * Service interface for handling email communication.
 * Defines the contract for sending automated notifications based on user operations.
 */
public interface EmailService {
    /**
     * Sends an email notification to the specified recipient.
     *
     * @param to        the recipient's email address.
     * @param operation the type of operation that triggered the notification.
     */
    void sendEmail(String to, OperationType operation);
}
