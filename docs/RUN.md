# Running & Testing

## 1. Infrastructure (Docker)

| Command | Description |
| :--- | :--- |

---

## 2. Code Quality & Verification

| Command | Description |
| :--- | :--- |
| `./mvnw spotless:apply` | Apply Google Java Style code formatting and remove unused imports |
| `./mvnw spotless:check` | Check code formatting compliance |
| `./mvnw checkstyle:check` | Validate Checkstyle rules |
| `./mvnw pmd:check` | Execute PMD static code analysis |
| `./mvnw test` | Execute domain unit tests and ArchUnit architecture tests |
| `./mvnw jacoco:report jacoco:check` | Generate coverage report and enforce $\ge 85\%$ coverage gate |
| `./mvnw spotless:apply checkstyle:check pmd:check verify -Dsonar.skip=true` | Run full CI quality verification in standalone mode |
| `./mvnw spotless:apply checkstyle:check pmd:check verify` | Run full CI quality verification with SonarQube |

---

## 3. Run Application

| Command | Description |
| :--- | :--- |
| `./mvnw spring-boot:run` | Start application via Spring Boot Maven plugin |
| `./mvnw clean package -Dsonar.skip=true` | Build and package executable JAR |
| `java -jar target/oulearning-0.0.1-SNAPSHOT.jar` | Run packaged executable JAR |
