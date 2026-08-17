package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate root representing the entire Organization at a specific point in time (Snapshot).
 * <p>
 * The organization hierarchy has N levels, starting with a single root Area (Level 1) with no parents.
 * </p>
 *
 * @param snapshotId the unique identifier of this organization snapshot
 * @param rootArea   the single root Area representing the Organization itself
 * @param createdAt  the timestamp when this organization snapshot was created
 */
public record Organization(SnapshotId snapshotId, Area rootArea, Instant createdAt) {

    /**
     * Compact constructor enforcing non-null fields, single root invariant, and tree structure integrity.
     */
    public Organization {
        if (snapshotId == null) {
            throw new InvalidOrganizationException("SnapshotId cannot be null");
        }
        if (rootArea == null) {
            throw new InvalidOrganizationException("Root Area cannot be null");
        }
        if (createdAt == null) {
            throw new InvalidOrganizationException("CreatedAt timestamp cannot be null");
        }
        if (!rootArea.isRoot()) {
            throw new InvalidOrganizationException(
                    "Organization root Area '%s' must have no parent IDs (actual: %s)"
                            .formatted(rootArea.name().value(), rootArea.parentIds()));
        }

        // Validate tree integrity (no cycles)
        validateTreeIntegrity(rootArea);
    }

    private static void validateTreeIntegrity(Area root) {
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

            if (current instanceof Area area) {
                queue.addAll(area.loadedChildren());
            }
        }
    }

    /**
     * Finds an organizational unit anywhere in the hierarchy by its {@link OuId}.
     *
     * @param id the OU ID to search for
     * @return an {@link Optional} containing the OU if found, or empty
     */
    public Optional<OrganizationalUnit> findOu(OuId id) {
        if (id == null) {
            return Optional.empty();
        }
        return allOus().stream().filter(ou -> ou.id().equals(id)).findFirst();
    }

    /**
     * Finds an organizational unit anywhere in the hierarchy by its {@link OuName}.
     *
     * @param name the OU Name to search for
     * @return an {@link Optional} containing the OU if found, or empty
     */
    public Optional<OrganizationalUnit> findOu(OuName name) {
        if (name == null) {
            return Optional.empty();
        }
        return allOus().stream().filter(ou -> ou.name().equals(name)).findFirst();
    }

    /**
     * Collects all organizational units across the entire hierarchy into an unmodifiable set.
     *
     * @return an unmodifiable {@link Set} of all {@link OrganizationalUnit}s in the organization
     */
    public Set<OrganizationalUnit> allOus() {
        final var result = new HashSet<OrganizationalUnit>();
        final var queue = new ArrayDeque<OrganizationalUnit>();
        queue.add(rootArea);

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            result.add(current);
            if (current instanceof Area area) {
                queue.addAll(area.loadedChildren());
            }
        }

        return Set.copyOf(result);
    }

    /**
     * @return the total number of organizational units in this organization snapshot
     */
    public int totalOusCount() {
        return allOus().size();
    }

    /**
     * Calculates the maximum depth of the hierarchy (root level = 1).
     *
     * @return the maximum hierarchy depth
     */
    public int depth() {
        return calculateDepth(rootArea);
    }

    private int calculateDepth(OrganizationalUnit unit) {
        if (unit instanceof Area area && !area.loadedChildren().isEmpty()) {
            return 1
                    + area.loadedChildren().stream()
                            .mapToInt(this::calculateDepth)
                            .max()
                            .orElse(0);
        }
        return 1;
    }

    /**
     * @return the total assigned budget of the organization (the root area's budget)
     */
    public Money totalBudget() {
        return rootArea.budget();
    }

    /**
     * Calculates the total combined budget of a collection of organizational units.
     *
     * @param ous the collection of organizational units
     * @return the combined {@link Money} budget
     */
    public Money totalBudgetOf(Collection<? extends OrganizationalUnit> ous) {
        if (ous == null || ous.isEmpty()) {
            return Money.zero(totalBudget().currency());
        }
        return ous.stream()
                .filter(Objects::nonNull)
                .map(OrganizationalUnit::budget)
                .reduce(Money.zero(totalBudget().currency()), Money::plus);
    }

    /**
     * Calculates the total budget of a specific OU and its entire subtree within this organization.
     *
     * @param ouId the {@link OuId} of the target organizational unit
     * @return the subtree {@link Money} budget
     */
    public Money subtreeBudgetOf(OuId ouId) {
        return findOu(ouId)
                .map(OrganizationalUnit::totalSubtreeBudget)
                .orElseThrow(() -> new InvalidOuException(
                        "OU with ID '%s' not found in organization snapshot '%s'"
                                .formatted(ouId, snapshotId)));
    }
}
