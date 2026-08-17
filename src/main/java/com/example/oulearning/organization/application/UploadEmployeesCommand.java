package com.example.oulearning.organization.application;

/**
 * Command for uploading an employee list from file.
 */
public record UploadEmployeesCommand(
        String managerCorporateKey,
        byte[] employeeFileBytes,
        String employeeFilename) {}
