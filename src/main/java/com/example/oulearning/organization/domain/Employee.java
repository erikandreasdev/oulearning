package com.example.oulearning.organization.domain;

/**
 * Value object representing an employee within the organization.
 *
 * @param corporateKey the unique corporate identifier
 * @param fullName     the employee's full name (name and surname)
 * @param email        the employee's email address
 * @param role         the employee's role in the organization
 */
public record Employee(CorporateKey corporateKey, FullName fullName, Email email, EmployeeRole role) {

    /**
     * Compact constructor enforcing non-null employee components.
     */
    public Employee {
        if (corporateKey == null) {
            throw new InvalidEmployeeException("Corporate key cannot be null");
        }
        if (fullName == null) {
            throw new InvalidEmployeeException("Full name cannot be null");
        }
        if (email == null) {
            throw new InvalidEmployeeException("Email cannot be null");
        }
        if (role == null) {
            throw new InvalidEmployeeException("Employee role cannot be null");
        }
    }

    /**
     * Factory method creating an {@link Employee} from typed domain value objects.
     *
     * @param corporateKey the {@link CorporateKey} value object
     * @param fullName     the {@link FullName} value object
     * @param email        the {@link Email} value object
     * @param role         the {@link EmployeeRole} enum
     * @return an {@link Employee} value object
     */
    public static Employee of(CorporateKey corporateKey, FullName fullName, Email email, EmployeeRole role) {
        return new Employee(corporateKey, fullName, email, role);
    }

    /**
     * Convenience factory method creating an {@link Employee} from raw strings and a role.
     *
     * @param corporateKey the raw corporate key string
     * @param name         the raw given name string
     * @param surname      the raw surname string
     * @param email        the raw email string
     * @param role         the {@link EmployeeRole} enum
     * @return a validated {@link Employee} value object
     */
    public static Employee of(String corporateKey, String name, String surname, String email, EmployeeRole role) {
        return new Employee(
                CorporateKey.of(corporateKey),
                FullName.of(name, surname),
                Email.of(email),
                role);
    }

    /**
     * Convenience factory method creating an {@link Employee} from raw strings for all fields including the role.
     *
     * @param corporateKey the raw corporate key string
     * @param name         the raw given name string
     * @param surname      the raw surname string
     * @param email        the raw email string
     * @param role         the raw role string to be parsed
     * @return a validated {@link Employee} value object
     */
    public static Employee of(String corporateKey, String name, String surname, String email, String role) {
        return of(corporateKey, name, surname, email, EmployeeRole.parse(role));
    }
}
