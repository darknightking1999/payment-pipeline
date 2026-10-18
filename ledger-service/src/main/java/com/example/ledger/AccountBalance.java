package com.example.ledger;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "account_balances")
public class AccountBalance {

    @Id
    private String accountId;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPaid;

    protected AccountBalance() {}

    public AccountBalance(String accountId) {
        this.accountId = accountId;
        this.totalPaid = BigDecimal.ZERO;
    }

    public void add(BigDecimal amount) {
        this.totalPaid = this.totalPaid.add(amount);
    }

    public String getAccountId() { return accountId; }
    public BigDecimal getTotalPaid() { return totalPaid; }
}
