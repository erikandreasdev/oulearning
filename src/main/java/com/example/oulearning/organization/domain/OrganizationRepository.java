package com.example.oulearning.organization.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for saving and querying immutable {@link Organization} snapshots across history.
 */
public interface OrganizationRepository {

    /**
     * Persists a new {@link Organization} snapshot.
     *
     * @param organization the organization snapshot to save
     */
    void save(Organization organization);

    /**
     * Retrieves the latest active {@link Organization} snapshot.
     *
     * @return an {@link Optional} containing the latest organization snapshot if present
     */
    Optional<Organization> findLatest();

    /**
     * Retrieves a specific {@link Organization} snapshot by its {@link SnapshotId}.
     *
     * @param snapshotId the snapshot ID
     * @return an {@link Optional} containing the matching snapshot if found
     */
    Optional<Organization> findBySnapshotId(SnapshotId snapshotId);

    /**
     * Retrieves the {@link Organization} snapshot that was active at a specific point in time.
     *
     * @param timestamp the historical timestamp
     * @return an {@link Optional} containing the active snapshot at that time
     */
    Optional<Organization> findAt(Instant timestamp);

    /**
     * Retrieves all historical {@link Organization} snapshots in chronological order.
     *
     * @return an unmodifiable {@link List} of all historical snapshots
     */
    List<Organization> findAllHistory();
}
