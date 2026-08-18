package com.example.oulearning.organization.application.port.in.command;

/**
 * Command for uploading an organization hierarchy (and optional employee list) from files.
 */
public record UploadOrganizationSnapshotCommand(
        String managerCorporateKey,
        byte[] organizationFileBytes,
        String organizationFilename,
        byte[] employeeFileBytes,
        String employeeFilename) {}
