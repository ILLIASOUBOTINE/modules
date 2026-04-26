package org.example.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.OperationType;
import org.example.dto.UserEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing and sending user-related events to Apache Kafka.
 * <p>
 * This producer encapsulates the logic for publishing {@link UserEvent} messages
 * to a dedicated notification topic.
 */
@Slf4j
@Service
public class UserProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String topic;

    public UserProducer(KafkaTemplate<String, UserEvent> kafkaTemplate,
                        @Value("${app.kafka.topics.user-notifications}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * Wraps user data into a {@link UserEvent} and sends it to the configured Kafka topic.
     *
     * @param email     the email address of the user.
     * @param operation the type of operation performed.
     */
    public void sendEvent(String email, OperationType operation) {
        UserEvent event = new UserEvent(email, operation);
        log.info("Attempting to send event to topic {}: {}", topic, event);

        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent message to topic {} with offset {}",
                                topic, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message to topic {}", topic, ex);
                    }
                });
    }
}
