package org.example.service;

import org.example.dto.OperationType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link EmailService} that uses Spring's {@link JavaMailSender}
 * to dispatch simple text-based email messages.
 */
@Service
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Constructs a {@link SimpleMailMessage} with a localized body text
     * based on the {@link OperationType} and sends it.
     * * <p>The email content is determined using a switch expression to match
     * specific business scenarios (e.g., account creation or deletion).</p>
     */
    @Override
    public void sendEmail(String to, OperationType operation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Уведомление!");

        String text = switch (operation) {
            case CREATE -> "Здравствуйте! Ваш аккаунт на сайте успешно создан.";
            case DELETE -> "Здравствуйте! Ваш аккаунт был удалён.";
        };

        message.setText(text);
        mailSender.send(message);
    }
}
