package com.example.oulearning.organization.domain.organization;

import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationException;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate root representing the entire Organization at a specific point in time (Snapshot).
 * Tracks the complete set of OU IDs belonging to the snapshot, root OU, and lifecycle status.
 *
 * @param snapshotId the unique identifier of this organization snapshot
 * @param rootOu     the single root OrganizationalUnit representing the Organization itself
 * @param ouIds      the set of all OrganizationalUnit IDs within this snapshot
 * @param status     the status lifecycle (ACTIVE or ARCHIVED)
 * @param createdAt  the timestamp when this organization snapshot was created
 */
public record Organization(
        SnapshotId snapshotId,
        OrganizationalUnit rootOu,
        Set<OuId> ouIds,
        SnapshotStatus status,
        Instant createdAt) {

    public Organization {
        if (snapshotId == null) {
            throw new InvalidOrganizationException("SnapshotId cannot be null");
        }
        if (rootOu == null) {
            throw new InvalidOrganizationException("Root OrganizationalUnit cannot be null");
        }
        if (ouIds == null) {
            throw new InvalidOrganizationException("OuIds set cannot be null");
        }
        if (status == null) {
            throw new InvalidOrganizationException("SnapshotStatus cannot be null");
        }
        if (createdAt == null) {
            throw new InvalidOrganizationException("CreatedAt timestamp cannot be null");
        }
        if (!rootOu.isRoot()) {
            throw new InvalidOrganizationException(
                    "Organization root OU '%s' must have no parent ID (actual: %s)"
                            .formatted(rootOu.name().value(), rootOu.parentId()));
        }

        ouIds = Set.copyOf(ouIds);

        // Validate tree integrity (no cycles) and ensure all loaded units are in ouIds
        final var treeIds = collectAndValidateTree(rootOu);
        if (!ouIds.containsAll(treeIds)) {
            throw new InvalidOrganizationException(
                    "ouIds set does not contain all OrganizationalUnits from the hierarchy tree");
        }
    }

    public Organization(SnapshotId snapshotId, OrganizationalUnit rootOu, Instant createdAt) {
        this(snapshotId, rootOu, collectAndValidateTree(rootOu), SnapshotStatus.ACTIVE, createdAt);
    }

    public Organization(SnapshotId snapshotId, OrganizationalUnit rootOu, SnapshotStatus status, Instant createdAt) {
        this(snapshotId, rootOu, collectAndValidateTree(rootOu), status, createdAt);
    }

    public static Organization active(SnapshotId snapshotId, OrganizationalUnit rootOu, Instant createdAt) {
        return new Organization(snapshotId, rootOu, collectAndValidateTree(rootOu), SnapshotStatus.ACTIVE, createdAt);
    }

    public static Organization active(SnapshotId snapshotId, OrganizationalUnit rootOu, Set<OuId> ouIds, Instant createdAt) {
        return new Organization(snapshotId, rootOu, ouIds, SnapshotStatus.ACTIVE, createdAt);
    }

    public Organization archive() {
        return new Organization(snapshotId, rootOu, ouIds, SnapshotStatus.ARCHIVED, createdAt);
    }

    public boolean isActive() {
        return status == SnapshotStatus.ACTIVE;
    }

    public boolean containsOu(OuId ouId) {
        if (ouId == null) {
            return false;
        }
        return ouIds.contains(ouId);
    }

    private static Set<OuId> collectAndValidateTree(OrganizationalUnit root) {
        if (root == null) {
            return Set.of();
        }
        final var visited = new HashSet<OuId>();
        final var queue = new ArrayDeque<OrganizationalUnit>();
        queue.add(root);

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            if (!visited.add(current.id())) {
                throw new InvalidOrganizationException(
                        "Cyclic reference detected in organization hierarchy at OU '%s' (%s)"
                                .formatted(current.name().value(), current.id()));
            }

            queue.addAll(current.loadedChildren());
        }
        return Set.copyOf(visited);
    }

    public Optional<OrganizationalUnit> findOu(OuId id) {
        if (id == null) {
            return Optional.empty();
        }
        return allOus().stream().filter(ou -> ou.id().equals(id)).findFirst();
    }

    public Optional<OrganizationalUnit> findOu(OuName name) {
        if (name == null) {
            return Optional.empty();
        }
        return allOus().stream().filter(ou -> ou.name().equals(name)).findFirst();
    }

    public Set<OrganizationalUnit> allOus() {
        final var result = new HashSet<OrganizationalUnit>();
        final var queue = new ArrayDeque<OrganizationalUnit>();
        queue.add(rootOu);

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            result.add(current);
            queue.addAll(current.loadedChildren());
        }

        return Set.copyOf(result);
    }

    public int totalOusCount() {
        return ouIds.size();
    }

    public int depth() {
        return calculateDepth(rootOu);
    }

    private int calculateDepth(OrganizationalUnit unit) {
        if (!unit.loadedChildren().isEmpty()) {
            return 1
                    + unit.loadedChildren().stream()
                            .mapToInt(this::calculateDepth)
                            .max()
                            .orElse(0);
        }
        return 1;
    }
}
