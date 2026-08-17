package com.example.oulearning.organization.domain.unit;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.exception.InvalidOuException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Domain model representing an Organizational Unit within the organization hierarchy.
 * Supports N-level hierarchies from root organizations down to leaf teams.
 *
 * @param id             the strongly-typed identifier of the organizational unit
 * @param name           the name of the organizational unit
 * @param type           the classification type (ORGANIZATION, AREA, SUBAREA)
 * @param owners         the set of corporate keys owning/managing this organizational unit
 * @param parentIds      the set of parent OU identifiers (empty for root organization)
 * @param childIds       the set of child OU identifiers (empty for leaf units)
 * @param loadedChildren the set of loaded child {@link OrganizationalUnit}s (empty if subtree not loaded)
 */
public record OrganizationalUnit(
        OuId id,
        OuName name,
        OuType type,
        Set<CorporateKey> owners,
        Set<OuId> parentIds,
        Set<OuId> childIds,
        Set<OrganizationalUnit> loadedChildren) {

    /**
     * Compact constructor enforcing non-null invariants and set immutability.
     */
    public OrganizationalUnit {
        if (id == null) {
            throw new InvalidOuException("OrganizationalUnit ID cannot be null");
        }
        if (name == null) {
            throw new InvalidOuException("OrganizationalUnit name cannot be null");
        }
        if (type == null) {
            throw new InvalidOuException("OrganizationalUnit type cannot be null");
        }
        if (owners == null) {
            throw new InvalidOuException("OrganizationalUnit owners cannot be null");
        }
        if (parentIds == null) {
            throw new InvalidOuException("OrganizationalUnit parent IDs cannot be null");
        }
        if (childIds == null) {
            throw new InvalidOuException("OrganizationalUnit child IDs cannot be null");
        }
        if (loadedChildren == null) {
            throw new InvalidOuException("OrganizationalUnit loaded children cannot be null");
        }

        owners = Set.copyOf(owners);
        parentIds = Set.copyOf(parentIds);
        childIds = Set.copyOf(childIds);
        loadedChildren = Set.copyOf(loadedChildren);

        if (!loadedChildren.isEmpty()) {
            final var loadedIds =
                    loadedChildren.stream().map(OrganizationalUnit::id).collect(Collectors.toSet());
            if (!childIds.containsAll(loadedIds)) {
                throw new InvalidOuException("Loaded children contain IDs not registered in childIds");
            }
        }
    }

    public boolean isRoot() {
        return parentIds.isEmpty();
    }

    public boolean isLeaf() {
        return childIds.isEmpty();
    }

    public boolean isSubtreeLoaded() {
        return childIds.isEmpty() || loadedChildren.size() == childIds.size();
    }

    public static OrganizationalUnit leaf(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds) {
        return new OrganizationalUnit(
                id,
                name,
                OuType.SUBAREA,
                owners,
                parentIds,
                Set.of(),
                Set.of());
    }

    public static OrganizationalUnit of(
            OuId id,
            OuName name,
            OuType type,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Set<OuId> childIds) {
        return new OrganizationalUnit(
                id,
                name,
                type,
                owners,
                parentIds,
                childIds,
                Set.of());
    }

    public static OrganizationalUnit withChildren(
            OuId id,
            OuName name,
            OuType type,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Set<OrganizationalUnit> children) {
        final var ids = children == null
                ? Set.<OuId>of()
                : children.stream().map(OrganizationalUnit::id).collect(Collectors.toSet());
        final var loaded = children == null ? Set.<OrganizationalUnit>of() : children;
        return new OrganizationalUnit(id, name, type, owners, parentIds, ids, loaded);
    }
}
