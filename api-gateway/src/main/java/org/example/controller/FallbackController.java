package org.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST Controller providing fallback endpoints for the API Gateway.
 * <p>
 * This controller is triggered by the Circuit Breaker mechanism (Resilience4j)
 * whenever upstream microservices are unavailable, timing out, or failing.
 * It implements the "Graceful Degradation" pattern by providing meaningful
 * responses instead of raw connection errors.
 * </p>
 */
@RestController
@RequestMapping("/api/fallback")
public class FallbackController {

    /**
     * Handles fallbacks for the User Service.
     * <p>
     * This method is invoked when requests directed to the user-service
     * cannot be completed. It is mapped to handle all HTTP methods (GET, POST, etc.)
     * to prevent "Method Not Allowed" errors during service outages.
     * </p>
     *
     * @return a {@link Mono} containing a {@link ResponseEntity} with
     * HTTP 503 Service Unavailable status and a descriptive error message.
     */
    @RequestMapping("/user-service")
    public Mono<ResponseEntity<String>> userServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User Service сейчас недоступен. Пожалуйста, попробуйте позже."));
    }

    /**
     * Handles fallbacks for the Notification Service.
     * <p>
     * Prevents cascading failures within the system when the notification
     * infrastructure is unresponsive.
     * </p>
     *
     * @return a {@link Mono} containing a {@link ResponseEntity} with
     * HTTP 503 Service Unavailable status and a descriptive error message.
     */
    @RequestMapping("/notification-service")
    public Mono<ResponseEntity<String>> notificationServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Сервис уведомлений временно не отвечает."));
    }
}
