package com.example.oulearning.organization.domain.organization.repository;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for persisting and querying historical {@link Organization} snapshots.
 */
public interface OrganizationRepository {

    /**
     * Persists an organization snapshot.
     *
     * @param organization the organization snapshot to save
     */
    void save(Organization organization);

    /**
     * Finds the latest active organization snapshot.
     *
     * @return an {@link Optional} containing the latest organization snapshot if found, or empty
     */
    Optional<Organization> findLatest();

    /**
     * Finds a specific organization snapshot by its unique {@link SnapshotId}.
     *
     * @param snapshotId the snapshot ID
     * @return an {@link Optional} containing the snapshot if found, or empty
     */
    Optional<Organization> findBySnapshotId(SnapshotId snapshotId);

    /**
     * Finds the organization snapshot that was active at a specific point in history.
     *
     * @param timestamp the historical point in time
     * @return an {@link Optional} containing the snapshot active at that time, or empty
     */
    Optional<Organization> findAt(Instant timestamp);

    /**
     * Retrieves all historical organization snapshots ordered chronologically.
     *
     * @return an unmodifiable {@link List} of all organization snapshots
     */
    List<Organization> findAllHistory();
}
