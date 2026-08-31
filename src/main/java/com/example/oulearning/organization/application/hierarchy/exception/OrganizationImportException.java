package com.example.oulearning.organization.application.hierarchy.exception;

public class OrganizationImportException extends RuntimeException {

    public OrganizationImportException(final String message) {
        super(message);
    }

    public OrganizationImportException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
