---
description: Format, build, run the full test suite (unit, integration, ArchUnit), SonarQube analysis, and report honestly.
---

# /verify

1. Run `./mvnw spotless:apply`.
2. Run `./mvnw checkstyle:check` (verify zero Checkstyle errors).
3. Run `./mvnw pmd:check` (verify zero PMD static analysis violations).
4. Run `./mvnw verify` (tests, ArchUnit, JaCoCo coverage, SonarQube analysis & Quality Gate).
   - Ensure SonarQube is running (e.g., via Docker container on `http://localhost:9000`) so the Sonar Quality Gate evaluates and passes with zero open issues.
   - If SonarQube is offline or unavailable, explicitly declare when using `-Dsonar.skip=true`.
5. Report: command output summary, tests run/failed, ArchUnit status, Checkstyle status, PMD status, JaCoCo coverage gate status, and SonarQube Quality Gate status (including unresolved issues count). Do not paraphrase success; quote the actual summary line.
