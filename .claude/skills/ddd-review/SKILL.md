---
name: ddd-review
description: Apply when reviewing code, discussing design, or when the user asks whether a change fits DDD / Clean Architecture. Checklist for architectural review of this Java/Spring DDD + Clean Architecture codebase.
---

# DDD / Clean Architecture review checklist

When reviewing or planning, check and report explicitly:
1. Does any `domain` file import a framework? (Reject.)
2. Are invariants enforced inside the aggregate, or leaked into a use case/controller? (Move them in.)
3. Any primitive obsession — raw `String`/`BigDecimal`/`UUID` for a domain concept? (Introduce a value object.)
4. Any use case touching more than one aggregate in a single transaction? (Use domain events.)
5. Any controller calling a repository or containing branching business logic? (Route through a use case.)
6. Any JPA entity or aggregate crossing an HTTP/message boundary? (Add DTOs/contracts.)
7. Any cross-context import of another context's domain classes? (Introduce ACL/events.)
8. Naming consistent with `docs/GLOSSARY.md`? Any new term needs a glossary entry.
9. Tests at the right level (domain unit vs. app fake vs. adapter integration)? ArchUnit updated?
10. Migration present and additive? `@Version` on new aggregate tables?

Format findings as: layer → file → issue → concrete fix. Prefer suggesting the smallest change that restores the boundary.
