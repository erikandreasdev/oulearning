---
description: Format, build, and run the full test suite (unit, integration, ArchUnit) and report honestly.
---

# /verify

1. Run `./mvnw spotless:apply`.
2. Run `./mvnw verify`.
3. If it fails: identify the failing tests/ArchUnit rules, explain the root cause, and fix the code — never the tests — unless the test itself is demonstrably wrong (explain why).
4. Re-run until green.
5. Report: command output summary, tests run/failed, ArchUnit status, coverage gate status. Do not paraphrase success; quote the actual summary line.
