---
description: Format, build, and run the full test suite (unit, integration, ArchUnit) and report honestly.
---

# /verify

1. Run `./mvnw spotless:apply`.
2. Run `./mvnw checkstyle:check` (verify zero Checkstyle errors).
3. Run `./mvnw pmd:check` (verify zero static code analysis / Sonar-equivalent quality issues).
4. Run `./mvnw verify` (tests, ArchUnit, JaCoCo coverage, static analysis).
5. Report: command output summary, tests run/failed, ArchUnit status, Checkstyle status, PMD status, coverage gate status. Do not paraphrase success; quote the actual summary line.

