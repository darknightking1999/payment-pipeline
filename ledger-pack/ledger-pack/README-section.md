## Ledger service: retry, dead-letter topic, idempotency

`ledger-service` consumes `payment.completed` and keeps a running `totalPaid` per account.

```powershell
Invoke-RestMethod http://localhost:8083/ledger/ACC1001
```

**How failures are handled**
- **Retry**: a failing message is retried 3 times, 1 second apart (`DefaultErrorHandler` + `FixedBackOff`).
- **Dead-letter topic**: after the retries it is published to `payment.completed.DLT` and the consumer moves on, so one bad message never blocks the partition. Unparseable JSON is sent straight to the DLT without retries.
- **Idempotent consumer**: Kafka delivers at-least-once, so duplicates are possible. The payment id is stored in `processed_events` in the same DB transaction as the balance update, so replaying an event does not change the balance twice.

**Try the failure path**: an account id starting with `FAIL` makes the ledger throw on purpose.

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8082/payments `
  -ContentType "application/json" `
  -Body '{"accountId":"FAIL-001","amount":100}'
docker compose logs -f ledger-service
```

**Try idempotency**: stop the ledger, rewind its offsets, start it again, and the balance stays the same.

```powershell
docker compose stop ledger-service
docker compose exec kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group ledger-service --topic payment.completed --reset-offsets --to-earliest --execute
docker compose start ledger-service
docker compose logs ledger-service
```

Known limitation: payment-service writes to Postgres and publishes to Kafka as two separate steps (dual write). The transactional outbox pattern would fix this.
