package com.example.oulearning.organization.domain.employee.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import org.instancio.Instancio;

public final class EmployeeTestFactory {

    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MAX_USERNAME_LENGTH = 12;
    private static final int MIN_DOMAIN_LENGTH = 3;
    private static final int MAX_DOMAIN_LENGTH = 8;
    private static final int MAX_NAME_GENERATION_LENGTH = 20;
    private static final int MAX_SURNAME_GENERATION_LENGTH = 20;
    private static final String EMAIL_FORMAT = "%s@%s.com";
    private static final String NON_ALPHANUMERIC_REGEX = "[^a-z0-9]";
    private static final String USERNAME_REPLACEMENT_CHAR = "a";
    private static final String DOMAIN_REPLACEMENT_CHAR = "b";

    private EmployeeTestFactory() {
    }

    public static long randomId() {
        return Instancio.gen().longs().range(EmployeeConstants.MIN_ID, Long.MAX_VALUE).get();
    }

    public static EmployeeId randomEmployeeId() {
        return EmployeeId.of(randomId());
    }

    public static String randomNameString() {
        return Instancio.gen()
                .string()
                .length(EmployeeConstants.MIN_NAME_LENGTH, MAX_NAME_GENERATION_LENGTH)
                .get();
    }

    public static String randomSurnameString() {
        return Instancio.gen()
                .string()
                .length(EmployeeConstants.MIN_SURNAME_LENGTH, MAX_SURNAME_GENERATION_LENGTH)
                .get();
    }

    public static String randomUsername() {
        return Instancio.gen()
                .string()
                .length(MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH)
                .get()
                .toLowerCase()
                .replaceAll(NON_ALPHANUMERIC_REGEX, USERNAME_REPLACEMENT_CHAR);
    }

    public static String randomDomain() {
        return Instancio.gen()
                .string()
                .length(MIN_DOMAIN_LENGTH, MAX_DOMAIN_LENGTH)
                .get()
                .toLowerCase()
                .replaceAll(NON_ALPHANUMERIC_REGEX, DOMAIN_REPLACEMENT_CHAR);
    }

    public static String randomEmailString() {
        return EMAIL_FORMAT.formatted(randomUsername(), randomDomain());
    }

    public static Name randomName() {
        return Name.of(randomNameString());
    }

    public static Surname randomSurname() {
        return Surname.of(randomSurnameString());
    }

    public static FullName randomFullName() {
        return FullName.of(randomName(), randomSurname());
    }

    public static Email randomEmail() {
        return Email.of(randomEmailString());
    }

    public static Employee randomEmployee() {
        return randomEmployee(randomEmployeeId());
    }

    public static Employee randomEmployee(final EmployeeId id) {
        return Employee.of(id, randomFullName(), randomEmail());
    }
}
