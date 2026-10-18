package com.example.ledger;

import jakarta.persistence.*;
import java.time.Instant;

/** One row per payment already applied to the ledger. Primary key = idempotency key. */
@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    private String paymentId;

    @Column(nullable = false)
    private Instant processedAt = Instant.now();

    protected ProcessedEvent() {}

    public ProcessedEvent(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() { return paymentId; }
    public Instant getProcessedAt() { return processedAt; }
}
