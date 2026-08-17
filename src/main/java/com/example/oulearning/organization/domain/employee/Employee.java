package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Optional;

/**
 * Entity/Aggregate representing an Employee and their OU membership.
 * Enforces the invariant that an employee belongs to at most one OU.
 */
public record Employee(
        CorporateKey corporateKey,
        FullName fullName,
        Email email,
        Phone phone,
        EmployeeRole role,
        OuId ouId) {

    public Employee {
        if (corporateKey == null) {
            throw new InvalidEmployeeException("CorporateKey cannot be null");
        }
        if (fullName == null) {
            throw new InvalidEmployeeException("FullName cannot be null");
        }
        if (email == null) {
            throw new InvalidEmployeeException("Email cannot be null");
        }
        if (role == null) {
            throw new InvalidEmployeeException("EmployeeRole cannot be null");
        }
        if (ouId == null) {
            throw new InvalidEmployeeException("OuId cannot be null - an employee must belong to an organizational unit");
        }
    }

    public Optional<Phone> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public Employee assignToOu(OuId newOuId) {
        if (newOuId == null) {
            throw new InvalidEmployeeException("New OuId cannot be null");
        }
        return new Employee(corporateKey, fullName, email, phone, role, newOuId);
    }

    public Employee updateDetails(FullName newFullName, Email newEmail, Phone newPhone, EmployeeRole newRole) {
        return new Employee(
                corporateKey,
                newFullName != null ? newFullName : fullName,
                newEmail != null ? newEmail : email,
                newPhone != null ? newPhone : phone,
                newRole != null ? newRole : role,
                ouId);
    }

    public static Employee of(
            CorporateKey corporateKey,
            FullName fullName,
            Email email,
            Phone phone,
            EmployeeRole role,
            OuId ouId) {
        return new Employee(corporateKey, fullName, email, phone, role, ouId);
    }

    public static Employee of(
            CorporateKey corporateKey,
            FullName fullName,
            Email email,
            EmployeeRole role,
            OuId ouId) {
        return new Employee(corporateKey, fullName, email, null, role, ouId);
    }
}
