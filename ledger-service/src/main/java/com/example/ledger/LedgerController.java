package com.example.ledger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LedgerController {

    private final AccountBalanceRepository balances;

    public LedgerController(AccountBalanceRepository balances) {
        this.balances = balances;
    }

    @GetMapping("/ledger/{accountId}")
    public ResponseEntity<AccountBalance> get(@PathVariable String accountId) {
        return balances.findById(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
