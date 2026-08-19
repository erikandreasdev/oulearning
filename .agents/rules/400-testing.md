---
trigger: glob
globs: **/src/test/**/*.java
description: Testing rules for all test sources.
---

# Testing

- Pyramid: many domain unit tests → application tests with fakes → few adapter/integration tests → very few e2e.
- Test method naming pattern: `givenA_whenB_thenC` where A, B, C accurately describe the Given, When, Then steps.
- Structure tests cleanly following given-when-then flow. Pure code only: no line or block comments.
- Dynamic test data via **Instancio** and centralized test factories (`*TestFactory.java`):
  - Every test must test randomized possibilities using Instancio.
  - Manual/literal variable assignments are ONLY allowed when testing specific edge cases/values where random generation is not appropriate.
  - Test-specific generation parameters (lengths, ranges, character replacements, formatting templates) must be encapsulated inside test factories, never in domain classes.
- Immutability and type inference: Use `final var` in test variables.
- Prefer fakes over mocks; mock only true external side effects. Never mock the class under test or value objects.
- AssertJ assertions on behavior/state, not internal interactions, unless the interaction is the contract.
- Integration tests use Testcontainers with the real DB/broker. No H2, no embedded substitutes.
- Every bug fix ships with a regression test written first.
- ArchUnit suite (`architecture` package) is mandatory; extend it when adding packages or contexts.
- Never `@Disabled` without an issue link and reason. Never lower coverage gates.
- Done means `./mvnw verify` is green.
