package com.example.oulearning.organization.application;

import java.util.UUID;

/**
 * Use case interface for uploading and activating a new organization hierarchy snapshot.
 */
public interface UploadOrganizationSnapshotUseCase {

    UUID execute(UploadOrganizationSnapshotCommand command);
}
