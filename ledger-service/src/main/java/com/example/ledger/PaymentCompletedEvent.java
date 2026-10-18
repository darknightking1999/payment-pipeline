package com.example.ledger;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(
        String paymentId,
        String accountId,
        BigDecimal amount,
        Instant completedAt) {}
