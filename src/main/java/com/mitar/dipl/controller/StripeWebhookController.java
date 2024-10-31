package com.mitar.dipl.controller;

import com.mitar.dipl.service.TransactionService;
import com.mitar.dipl.utils.StripeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final TransactionService transactionService;
    private final StripeService stripeService;
    private final ObjectMapper objectMapper; // For JSON parsing

    public StripeWebhookController(TransactionService transactionService, StripeService stripeService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.stripeService = stripeService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {

        log.info("Webhook received");
        log.debug("Payload: {}", payload); // Log the raw payload for inspection

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature.");
        } catch (Exception e) {
            log.error("Unexpected error while constructing event.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error.");
        }

        log.info("Webhook received: {}", event.getId());
        log.info("Event type: {}", event.getType());

        // Handle the event based on its type
        switch (event.getType()) {
            case "checkout.session.completed":
                handleSessionEvent(event, payload, this::completeTransaction);
                break;
            case "checkout.session.async_payment_failed", "checkout.session.expired":
                handleSessionEvent(event, payload, this::failTransaction);
                break;
            // Add more cases as needed
            default:
                log.info("Unhandled event type: {}", event.getType());
                break;
        }

        // Return a 200 response to acknowledge receipt of the event
        return ResponseEntity.ok("Webhook received.");
    }

    /**
     * Handles session-based events by retrieving the Session object and invoking the appropriate handler.
     *
     * @param event   The Stripe event.
     * @param payload The raw JSON payload.
     * @param handler The handler function to process the session.
     */
    private void handleSessionEvent(Event event, String payload, SessionHandler handler) {
        log.info("Handling event type: {}", event.getType());
        Session session = retrieveSession(event, payload);
        if (session == null) {
            log.error("Failed to retrieve Session object for event: {}", event.getType());
            return;
        }

        String transactionId = session.getMetadata().get("transaction_id");
        if (transactionId == null) {
            log.warn("No transaction_id found in session metadata for event: {}", event.getType());
            return;
        }

        log.info("Processing Transaction ID: {}", transactionId);
        try {
            handler.handle(UUID.fromString(transactionId), session);
        } catch (Exception e) {
            log.error("Error processing transaction for ID: {}", transactionId, e);
        }
    }

    /**
     * Retrieves the Session object from the event. Attempts deserialization first; if it fails, retrieves it via the Stripe API.
     *
     * @param event   The Stripe event.
     * @param payload The raw JSON payload.
     * @return The Session object or null if retrieval fails.
     */
    private Session retrieveSession(Event event, String payload) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Session session = null;

        if (deserializer.getObject().isPresent()) {
            Object obj = deserializer.getObject().get();
            if (obj instanceof Session) {
                session = (Session) obj;
                log.info("Webhook: Deserialized Session object successfully.");
            } else {
                log.error("Webhook: Deserialized object is not an instance of Session. Type: {}", obj.getClass().getName());
            }
        } else {
            log.warn("Webhook: Deserializer did not return a Session object. Attempting to retrieve via API.");
            // Attempt to parse the session ID from the payload
            try {
                JsonNode jsonNode = objectMapper.readTree(payload);
                String sessionId = jsonNode.path("data").path("object").path("id").asText(null);
                if (sessionId == null) {
                    log.error("Webhook: Session ID not found in event data.");
                    return null;
                }
                log.info("Fetching Session ID from event data: {}", sessionId);
                session = stripeService.retrieveCheckoutSession(sessionId);
                log.info("Successfully retrieved session via API: {}", sessionId);
            } catch (StripeException e) {
                log.error("Failed to retrieve Checkout Session via API.", e);
            } catch (Exception e) {
                log.error("Failed to parse session ID from payload.", e);
            }
        }

        return session;
    }

    /**
     * Functional interface for handling Session events.
     */
    @FunctionalInterface
    interface SessionHandler {
        void handle(UUID transactionId, Session session) throws Exception;
    }

    /**
     * Completes the transaction based on the provided Session.
     *
     * @param transactionId The UUID of the transaction.
     * @param session       The Stripe Session object.
     */
    private void completeTransaction(UUID transactionId, Session session) {
        log.info("Webhook: Payment successful for Transaction ID: {}", transactionId);
        transactionService.completeTransaction(transactionId, session);
    }

    /**
     * Fails the transaction based on the provided Session.
     *
     * @param transactionId The UUID of the transaction.
     * @param session       The Stripe Session object.
     */
    private void failTransaction(UUID transactionId, Session session) {
        log.info("Webhook: Payment failed for Transaction ID: {}", transactionId);
        transactionService.failTransaction(transactionId, session);
    }
}
