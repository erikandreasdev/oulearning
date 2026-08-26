---
trigger: glob
globs: **/domain/**/*.java
description: Domain layer rules — aggregates, value objects, domain events, repository ports.
---

# Domain layer

- Pure Java. Zero framework imports: no Spring, JPA, Jackson, Lombok, Jakarta Validation.
- Every domain model class (aggregates, entities, value objects, domain events) must be an immutable `record` to leverage immutability, data security, and clarity.
- Aggregates own and enforce their invariants. No public setters. State changes go through intention-revealing methods (`order.confirm()`, not `setStatus`), which validate and return updated record states or register domain events.
- Value objects are immutable `record`s with validation in the compact constructor. Use them for every domain concept (`Money`, `Email`, `Quantity`, `FullName`), never raw primitives.
- Identity is a typed value object (`record TrainingId(Long value)`), never bare `Long`/`String`.
- Enforce validation and invariants strictly in domain code via centralized domain guards (`*Guard`) that throw domain-specific exceptions extending `DomainException`.
- All domain validations must be driven by configurable constants in vertical slice constants classes (`*Constants.java`) with zero magic numbers/strings in domain code.
- Domain events are immutable records named in past tense (`TrainingApproved`), carrying ids and relevant data — not whole aggregates. Aggregates register events (`pullDomainEvents()`); they never publish them.
- Repository interfaces (ports) live here, speak in aggregates, one per aggregate root: `Optional<Training> findById(TrainingId)`, `void save(Training)`. No query-method zoo — read models belong to application/infrastructure.
- Domain services only for logic spanning aggregates; stateless; take and return domain types.
- Domain exceptions extend the sealed `DomainException`, are specific, and carry context.
- Constructors private/package-private + static factories (`Training.create(...)`) plus a reconstitution factory for persistence mapping. Returned collections are unmodifiable (`Set.copyOf`, `Collections.unmodifiableSet`).
- Entities/aggregates: `equals`/`hashCode` on identity only. Value objects: on all fields (records do this automatically).
- Tests: pure JUnit + AssertJ, no Spring context, no mocks. Test behavior through the public API.
