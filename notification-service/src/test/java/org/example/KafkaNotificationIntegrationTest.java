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
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "app.kafka.topics.user-notifications=user-notifications",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=org.example.dto",
        "spring.kafka.consumer.properties.spring.json.value.default.type=org.example.dto.UserEvent"
})
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {
                "log.dir=target/embedded-kafka",
                "metadata.log.dir=target/embedded-kafka-metadata",
                "auto.create.topics.enable=true"
        }
)
public class KafkaNotificationIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
            .withPerMethodLifecycle(true);

    @Test
    void testKafkaMessageTriggersEmail() throws ExecutionException, InterruptedException {
        String userEmail = "test@example.com";
        UserEvent event = new UserEvent(userEmail, OperationType.CREATE);

        kafkaTemplate.send("user-notifications", event).get();
        kafkaTemplate.flush();

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

            assertThat(receivedMessages).hasSize(1);

            MimeMessage message = receivedMessages[0];
            assertThat(message.getAllRecipients()[0].toString()).isEqualTo(userEmail);
            assertThat(message.getContent().toString()).contains("создан");
        });
    }
}
