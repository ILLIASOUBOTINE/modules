package org.example.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserEvent;
import org.example.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service responsible for processing user-related events.
 * <p>
 * This service listens to a Kafka topic and delegates the event processing
 * to the {@link EmailService} to notify users based on their actions.
 */
@Slf4j
@Service
public class NotificationConsumer {
    private final EmailService emailService;

    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Consumes messages from the Kafka topic.
     * <p>
     * This method is automatically invoked whenever a new {@link UserEvent}
     * is published to the "user-notifications" topic. It extracts the event
     * details and triggers an email notification.
     *
     * @param event the {@link UserEvent} received from the Kafka broker.
     */
    @KafkaListener(topics = "${app.kafka.topics.user-notifications}", groupId = "notification-group")
    public void consume(UserEvent event) {
        log.info("Received event from Kafka: {}", event);
        try {
            emailService.sendEmail(event.email(), event.operationType());
            log.info("Email notification processed for: {}", event.email());
        } catch (Exception e) {
            log.error("Error processing email notification for event: {}", event, e);
        }
    }
}
