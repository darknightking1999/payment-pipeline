package com.example.ledger;

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
    private final LedgerService ledgerService;

    public PaymentEventListener(ObjectMapper objectMapper, LedgerService ledgerService) {
        this.objectMapper = objectMapper;
        this.ledgerService = ledgerService;
    }

    @KafkaListener(topics = KafkaConfig.TOPIC)
    public void onPaymentCompleted(String message) throws JsonProcessingException {
        PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
        log.info("Received payment {} for account {}", event.paymentId(), event.accountId());
        ledgerService.apply(event);
    }

    /** Just logs what lands in the dead-letter topic. A real system would alert or park it for manual replay. */
    @KafkaListener(topics = KafkaConfig.DLT, groupId = "ledger-service-dlt")
    public void onDeadLetter(String message) {
        log.error("DEAD LETTER received (gave up after retries): {}", message);
    }
}
