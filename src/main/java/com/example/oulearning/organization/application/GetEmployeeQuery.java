package com.example.oulearning.organization.application;

import java.util.Objects;

/**
 * Query to retrieve an Employee by their CorporateKey.
 */
public record GetEmployeeQuery(String corporateKey) {

    public GetEmployeeQuery {
        Objects.requireNonNull(corporateKey, "corporateKey cannot be null");
    }
}
