package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.instancio.Instancio;

/**
 * Test data generators using Instancio to provide dynamic domain test objects.
 */
public final class DomainGenerators {

    private DomainGenerators() {
        // Utility class
    }

    public static String randomAlphabetic(int minLength, int maxLength) {
        return Instancio.gen().string().mixedCase().length(minLength, maxLength).get();
    }

    public static Name randomName() {
        return Name.of(randomAlphabetic(3, 15));
    }

    public static Surname randomSurname() {
        return Surname.of(randomAlphabetic(3, 15));
    }

    public static Email randomEmail() {
        final var local = Instancio.gen().string().lowerCase().length(4, 10).get();
        final var domain = Instancio.gen().string().lowerCase().length(4, 10).get();
        return Email.of("%s@%s.com".formatted(local, domain));
    }

    public static Phone randomPhone() {
        final var digits = Instancio.gen().longs().range(1000000000L, 9999999999L).get();
        return Phone.of("+%d".formatted(digits));
    }

    public static CorporateKey randomCorporateKey() {
        final var number = Instancio.gen().ints().range(0, 9999).get();
        return CorporateKey.of("CK%04d".formatted(number));
    }

    public static EmployeeRole randomEmployeeRole() {
        return Instancio.gen().enumOf(EmployeeRole.class).get();
    }

    public static FullName randomFullName() {
        return FullName.of(randomName(), randomSurname());
    }

    public static Employee randomEmployee() {
        return Employee.of(randomCorporateKey(), randomFullName(), randomEmail(), randomEmployeeRole());
    }

    public static Money randomMoney() {
        final var amount = BigDecimal.valueOf(Instancio.gen().doubles().range(100.0, 10000.0).get());
        return Money.euros(amount);
    }

    public static OuId randomOuId() {
        return OuId.of(UUID.randomUUID());
    }

    public static OuName randomOuName() {
        return OuName.of("OU " + randomAlphabetic(3, 12));
    }

    public static SnapshotId randomSnapshotId() {
        return SnapshotId.of(UUID.randomUUID());
    }

    public static Subarea randomSubarea() {
        return Subarea.of(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(randomOuId()),
                randomMoney());
    }

    public static Area randomArea() {
        final var sub1 = randomSubarea();
        final var sub2 = randomSubarea();
        final var totalBudget = sub1.budget().plus(sub2.budget());
        return Area.withChildren(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(randomOuId()),
                totalBudget,
                Set.of(sub1, sub2));
    }

    public static Organization randomOrganization() {
        final var rootAreaId = randomOuId();
        final var sub1 = Subarea.of(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(rootAreaId),
                Money.euros(5000.00));
        final var sub2 = Subarea.of(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(rootAreaId),
                Money.euros(5000.00));
        final var rootArea = Area.withChildren(
                rootAreaId,
                OuName.of("Headquarters"),
                Set.of(randomCorporateKey()),
                Set.of(), // root has no parents
                Money.euros(10000.00),
                Set.of(sub1, sub2));
        return new Organization(randomSnapshotId(), rootArea, Instant.now());
    }
}
