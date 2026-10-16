package com.example.payment;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentEventPublisher publisher;

    public PaymentService(PaymentRepository repository, PaymentEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Transactional
    public Payment create(PaymentRequest request) {
        Payment saved = repository.save(new Payment(request.accountId(), request.amount(), "COMPLETED"));
        publisher.publish(new PaymentCompletedEvent(
                saved.getId().toString(), saved.getAccountId(), saved.getAmount(), Instant.now()));
        return saved;
    }
}
