package org.example.kafka.producer;

import org.example.dto.OperationType;
import org.example.dto.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing and sending user-related events to Apache Kafka.
 * <p>
 * This producer encapsulates the logic for publishing {@link UserEvent} messages
 * to a dedicated notification topic.
 */
@Service
public class UserProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String TOPIC = "user-notifications";

    public UserProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Wraps user data into a {@link UserEvent} and sends it to the Kafka topic.
     *
     * @param email     the email address of the user associated with the event.
     * @param operation the type of operation performed (e.g., CREATE, UPDATE, DELETE).
     */
    public void sendEvent(String email, OperationType operation) {
        kafkaTemplate.send(TOPIC, new UserEvent(email, operation));
    }
}
