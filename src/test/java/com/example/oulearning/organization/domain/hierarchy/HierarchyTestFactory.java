package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import java.util.Set;
import java.util.UUID;
import org.instancio.Instancio;

public final class HierarchyTestFactory {

    private HierarchyTestFactory() {
    }

    public static UUID randomUuid() {
        return Instancio.create(UUID.class);
    }

    public static OrganizationalUnitId randomOrganizationalUnitId() {
        return OrganizationalUnitId.of(randomUuid());
    }

    public static String randomOrganizationalUnitNameString() {
        return Instancio.gen()
                .string()
                .length(HierarchyConstants.MIN_NAME_LENGTH, HierarchyConstants.MAX_NAME_LENGTH)
                .get();
    }

    public static Name randomName() {
        return Name.of(randomOrganizationalUnitNameString());
    }

    public static OrganizationalUnit randomOrganizationalUnit() {
        return randomOrganizationalUnit(randomOrganizationalUnitId());
    }

    public static OrganizationalUnit randomOrganizationalUnit(final OrganizationalUnitId id) {
        return OrganizationalUnit.of(
                id,
                randomName(),
                randomOrganizationalUnitId(),
                Set.of(randomOrganizationalUnitId()),
                Set.of(EmployeeTestFactory.randomEmployeeId()),
                Set.of(EmployeeTestFactory.randomEmployeeId()));
    }

    public static Organization randomOrganization() {
        return new Organization(Set.of(randomOrganizationalUnitId(), randomOrganizationalUnitId()));
    }
}
