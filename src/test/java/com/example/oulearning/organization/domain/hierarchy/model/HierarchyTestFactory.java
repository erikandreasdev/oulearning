package com.example.oulearning.organization.domain.hierarchy.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import java.util.Set;
import org.instancio.Instancio;

public final class HierarchyTestFactory {

    private HierarchyTestFactory() {
    }

    public static long randomId() {
        return Instancio.gen().longs().range(HierarchyConstants.MIN_ID, Long.MAX_VALUE).get();
    }

    public static OrganizationalUnitId randomOrganizationalUnitId() {
        return OrganizationalUnitId.of(randomId());
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
