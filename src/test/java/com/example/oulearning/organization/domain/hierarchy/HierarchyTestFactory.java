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

    public static OuId randomOuId() {
        return OuId.of(randomUuid());
    }

    public static String randomOuNameString() {
        return Instancio.gen()
                .string()
                .length(HierarchyConstants.MIN_NAME_LENGTH, HierarchyConstants.MAX_NAME_LENGTH)
                .get();
    }

    public static Name randomName() {
        return Name.of(randomOuNameString());
    }

    public static Ou randomOu() {
        return randomOu(randomOuId());
    }

    public static Ou randomOu(final OuId id) {
        return Ou.of(
                id,
                randomName(),
                randomOuId(),
                Set.of(randomOuId()),
                Set.of(EmployeeTestFactory.randomEmployeeId()),
                Set.of(EmployeeTestFactory.randomEmployeeId()));
    }

    public static Organization randomOrganization() {
        return new Organization(Set.of(randomOuId(), randomOuId()));
    }
}
