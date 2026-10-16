package com.example.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final ObjectMapper objectMapper;

    public PaymentEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment.completed")
    public void onPaymentCompleted(String message) throws JsonProcessingException {
        PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
        // Mock "send": in real life this would call an SMS / WhatsApp API
        log.info("SMS -> account {}: payment of {} received (ref {})",
                event.accountId(), event.amount(), event.paymentId());
    }
}
