package com.example.oulearning.organization.application;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable application query for retrieving an Organization snapshot.
 */
public record GetOrganizationSnapshotQuery(
        UUID snapshotId,
        Instant atTimestamp) {

    public static GetOrganizationSnapshotQuery byId(UUID snapshotId) {
        return new GetOrganizationSnapshotQuery(snapshotId, null);
    }

    public static GetOrganizationSnapshotQuery at(Instant atTimestamp) {
        return new GetOrganizationSnapshotQuery(null, atTimestamp);
    }
}
