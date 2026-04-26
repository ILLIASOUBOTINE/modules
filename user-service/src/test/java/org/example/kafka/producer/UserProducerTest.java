package org.example.kafka.producer;

import org.example.dto.OperationType;
import org.example.dto.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProducerTest {

    @Mock
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    private UserProducer userProducer;

    private final String testTopic = "test-topic";

    @BeforeEach
    void setUp() {
        userProducer = new UserProducer(kafkaTemplate, testTopic);
    }

    @Test
    void sendEvent_ShouldSendToCorrectTopic() {
        // Arrange
        String email = "test@example.com";
        OperationType operation = OperationType.CREATE;

        CompletableFuture<SendResult<String, UserEvent>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(eq(testTopic), any(UserEvent.class)))
                .thenReturn(future);

        // Act
        userProducer.sendEvent(email, operation);

        // Assert
        verify(kafkaTemplate).send(eq(testTopic), any(UserEvent.class));
    }
}


