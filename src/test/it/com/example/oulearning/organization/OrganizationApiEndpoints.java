package com.example.oulearning.organization;

public final class OrganizationApiEndpoints {
    private OrganizationApiEndpoints() {}
    public static final String EMPLOYEES = "/api/v1/employees";
    public static final String EMPLOYEE_BY_ID = "/api/v1/employees/%s";
    public static final String ORGANIZATIONAL_UNITS = "/api/v1/organizational-units";
    public static final String ORGANIZATIONAL_UNIT_BY_ID = "/api/v1/organizational-units/%s";
    public static final String ORGANIZATIONAL_UNIT_MEMBERS = "/api/v1/organizational-units/%s/members";
}
