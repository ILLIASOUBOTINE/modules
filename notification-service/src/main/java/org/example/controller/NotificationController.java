package org.example.controller;

import org.example.dto.UserEvent;
import org.example.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that provides endpoints for triggering manual email notifications.
 * <p>
 * This controller acts as an entry point for external systems or testing tools
 * to initiate email delivery via the {@link EmailService}.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles HTTP POST requests to send an email notification based on the provided event data.
     *
     * @param event the {@link UserEvent} payload containing the recipient's email
     * and the type of operation performed.
     * @return a {@link ResponseEntity} containing a success message if the notification
     * was successfully handed off to the mail server.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody UserEvent event) {
        emailService.sendEmail(event.email(), event.operationType());
        return ResponseEntity.ok("Notification sent successfully to Mail!");
    }
}
