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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotificationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
            .withPerMethodLifecycle(true);

    @Test
    void testDirectApiSendsEmail() throws Exception {
        UserEvent event = new UserEvent("test@mail.com", OperationType.CREATE);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/notifications/send", event, String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);

        MimeMessage message = receivedMessages[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("test@mail.com");
        assertThat(message.getSubject()).contains("Уведомление");
        assertThat(message.getContent().toString()).contains("создан");
    }
}
