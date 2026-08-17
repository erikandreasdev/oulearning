package com.example.oulearning.organization.domain;

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
}
