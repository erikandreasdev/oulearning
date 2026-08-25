package com.example.oulearning.organization.domain.employee.model;


public final class EmployeeConstants {

    public static final long MIN_ID = 1L;
    public static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MIN_SURNAME_LENGTH = 1;
    public static final int MAX_SURNAME_LENGTH = 50;
    public static final int MAX_FULL_NAME_LENGTH = 100;

    private EmployeeConstants() {}
}
