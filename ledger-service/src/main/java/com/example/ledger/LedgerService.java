package com.example.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);

    private final AccountBalanceRepository balances;
    private final ProcessedEventRepository processed;

    public LedgerService(AccountBalanceRepository balances, ProcessedEventRepository processed) {
        this.balances = balances;
        this.processed = processed;
    }

    /**
     * Idempotent: the "already processed" marker and the balance update happen in ONE
     * transaction, so either both are saved or neither. Kafka delivers at-least-once,
     * so the same event can arrive twice (rebalance, consumer restart, offset reset).
     */
    @Transactional
    public void apply(PaymentCompletedEvent event) {
        // Demo hook: lets you trigger a failure on demand to see retry + dead-letter topic.
        if (event.accountId().startsWith("FAIL")) {
            throw new IllegalStateException("Simulated ledger failure for account " + event.accountId());
        }

        if (processed.existsById(event.paymentId())) {
            log.info("Duplicate event skipped, payment {} already applied", event.paymentId());
            return;
        }

        processed.save(new ProcessedEvent(event.paymentId()));

        AccountBalance balance = balances.findById(event.accountId())
                .orElseGet(() -> new AccountBalance(event.accountId()));
        balance.add(event.amount());
        balances.save(balance);

        log.info("Ledger updated: account {} totalPaid={} (payment {})",
                balance.getAccountId(), balance.getTotalPaid(), event.paymentId());
    }
}
