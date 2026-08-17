---
trigger: glob
globs: **/domain/**/*.java
description: Domain layer rules — aggregates, value objects, domain events, repository ports.
---

# Domain layer

- Pure Java. Zero framework imports: no Spring, JPA, Jackson, Lombok, Jakarta Validation.
- Aggregates own and enforce their invariants. No public setters. State changes go through intention-revealing methods (`order.confirm()`, not `setStatus`), which validate and may register domain events.
- Value objects are immutable `record`s with validation in the compact constructor. Use them for every domain concept (`Money`, `Email`, `Quantity`), never raw primitives.
- Identity is a typed value object (`record OrderId(UUID value)`), never bare `UUID`/`Long`/`String`.
- Domain events are immutable records named in past tense (`OrderConfirmed`), carrying ids and relevant data — not whole aggregates. Aggregates register events (`pullDomainEvents()`); they never publish them.
- Repository interfaces (ports) live here, speak in aggregates, one per aggregate root: `Optional<Order> findById(OrderId)`, `void save(Order)`. No query-method zoo — read models belong to application/infrastructure.
- Domain services only for logic spanning aggregates; stateless; take and return domain types.
- Domain exceptions extend the sealed `DomainException`, are specific, and carry context.
- Constructors private/package-private + static factories (`Order.place(...)`) plus a reconstitution factory for persistence mapping. Returned collections are unmodifiable.
- Entities/aggregates: `equals`/`hashCode` on identity only. Value objects: on all fields (records do this).
- Tests: pure JUnit + AssertJ, no Spring context, no mocks. Test behavior through the public API.
