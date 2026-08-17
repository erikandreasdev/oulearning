package com.example.oulearning.organization.application;

/**
 * Command for uploading an organization hierarchy (and optional employee list) from files.
 */
public record UploadOrganizationSnapshotCommand(
        String managerCorporateKey,
        byte[] organizationFileBytes,
        String organizationFilename,
        byte[] employeeFileBytes,
        String employeeFilename) {}
