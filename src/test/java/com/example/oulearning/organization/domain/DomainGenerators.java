package com.example.oulearning.organization.domain;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Name;
import com.example.oulearning.organization.domain.employee.Phone;
import com.example.oulearning.organization.domain.employee.Surname;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
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
        return Employee.of(randomCorporateKey(), randomFullName(), randomEmail(), randomPhone(), randomEmployeeRole(), randomOuId());
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

    public static OrganizationalUnit randomLeafOu() {
        return OrganizationalUnit.leaf(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(randomOuId()));
    }

    public static OrganizationalUnit randomOrganizationalUnit() {
        final var child1 = randomLeafOu();
        final var child2 = randomLeafOu();
        return OrganizationalUnit.withChildren(
                randomOuId(),
                randomOuName(),
                OuType.AREA,
                Set.of(randomCorporateKey()),
                Set.of(randomOuId()),
                Set.of(child1, child2));
    }

    public static Organization randomOrganization() {
        final var rootOuId = randomOuId();
        final var sub1 = OrganizationalUnit.leaf(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(rootOuId));
        final var sub2 = OrganizationalUnit.leaf(
                randomOuId(),
                randomOuName(),
                Set.of(randomCorporateKey()),
                Set.of(rootOuId));
        final var rootOu = OrganizationalUnit.withChildren(
                rootOuId,
                OuName.of("Headquarters"),
                OuType.ORGANIZATION,
                Set.of(randomCorporateKey()),
                Set.of(), // root has no parents
                Set.of(sub1, sub2));
        return new Organization(randomSnapshotId(), rootOu, Instant.now());
    }
}
