---
trigger: glob
globs: **/application/**/*.java
description: Application layer rules — use cases, commands/queries, ports, transactions.
---

# Application layer

- One class per use case. Input port interface `<Verb><Noun>UseCase` + implementation `<Verb><Noun>Service` (e.g. `PlaceOrderUseCase` / `PlaceOrderService`).
- Commands and queries are immutable records with primitive/VO fields; never domain objects inside commands. Results are small DTO records or ids.
- A use case orchestrates only: load aggregate via port → invoke domain behavior → save → publish events. Business rules stay in the domain.
- Transaction boundary = one use case = one aggregate mutated. Cross-aggregate consistency via domain events (outbox in infra), never one large transaction.
- Depends on `domain` only. Allowed Spring: `@Transactional`, optionally `@Service`. Constructor injection.
- Output ports (interfaces) for everything external: `OrderRepository`, `PaymentGateway`, `DomainEventPublisher`, `Clock`. Never reference adapters.
- Queries (CQRS-lite) are read-only, may use dedicated read ports/DTOs and bypass aggregates. Never mutate.
- Application exceptions (`OrderNotFoundException`) are specific; let domain exceptions propagate to the adapter's exception mapper.
- Tests: JUnit + in-memory fakes for ports (`InMemoryOrderRepository`). Mockito only for external side effects. No Spring context.
