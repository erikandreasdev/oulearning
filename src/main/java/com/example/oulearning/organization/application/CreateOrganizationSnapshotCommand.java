package com.example.oulearning.organization.application;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable application command for taking an organization hierarchy snapshot.
 */
public record CreateOrganizationSnapshotCommand(
        UUID snapshotId,
        UUID rootOuId,
        Instant createdAt) {}
