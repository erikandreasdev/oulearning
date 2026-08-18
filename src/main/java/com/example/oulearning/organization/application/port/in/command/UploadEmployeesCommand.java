package com.example.oulearning.organization.application.port.in.command;

/**
 * Command for uploading an employee list from file.
 */
public record UploadEmployeesCommand(
        String managerCorporateKey,
        byte[] employeeFileBytes,
        String employeeFilename) {}
