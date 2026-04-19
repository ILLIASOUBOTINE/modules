package org.example;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.example.dto.OperationType;
import org.example.dto.UserEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaNotificationIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
            .withPerMethodLifecycle(true);

    @Test
    void testKafkaMessageTriggersEmail() {
        String userEmail = "test@example.com";
        UserEvent event = new UserEvent(userEmail, OperationType.CREATE);

        kafkaTemplate.send("user-notifications", event);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

            assertThat(receivedMessages).hasSize(1);

            MimeMessage message = receivedMessages[0];
            assertThat(message.getAllRecipients()[0].toString()).isEqualTo(userEmail);
            assertThat(message.getContent().toString()).contains("создан");
        });
    }
}
