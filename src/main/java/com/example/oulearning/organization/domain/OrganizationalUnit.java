package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;
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
 * @param budget         the assigned budget for this organizational unit
 * @param childIds       the set of child OU identifiers (empty for leaf units)
 * @param loadedChildren the set of loaded child {@link OrganizationalUnit}s (empty if subtree not loaded)
 */
public record OrganizationalUnit(
        OuId id,
        OuName name,
        OuType type,
        Set<CorporateKey> owners,
        Set<OuId> parentIds,
        Money budget,
        Set<OuId> childIds,
        Set<OrganizationalUnit> loadedChildren) {

    /**
     * Compact constructor enforcing non-null invariants, set immutability, and child budget consistency.
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
        if (budget == null) {
            throw new InvalidOuException("OrganizationalUnit budget cannot be null");
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

            if (loadedChildren.size() == childIds.size()) {
                final var totalChildrenBudget = loadedChildren.stream()
                        .map(OrganizationalUnit::budget)
                        .reduce(Money.zero(budget.currency()), Money::plus);

                if (!totalChildrenBudget.equals(budget)) {
                    throw new OuBudgetMismatchException(
                            "OrganizationalUnit '%s' budget (%s) does not match the sum of its child OU budgets (%s)"
                                    .formatted(name.value(), budget, totalChildrenBudget));
                }
            }
        }
    }

    /**
     * Checks if this unit is a root organizational unit (has no parent OUs).
     *
     * @return {@code true} if parentIds is empty
     */
    public boolean isRoot() {
        return parentIds.isEmpty();
    }

    /**
     * Checks if this unit is a leaf organizational unit (has no child OUs).
     *
     * @return {@code true} if childIds is empty
     */
    public boolean isLeaf() {
        return childIds.isEmpty();
    }

    /**
     * Checks if all child OUs are loaded in this instance.
     *
     * @return {@code true} if childIds is empty or all child OU instances are loaded
     */
    public boolean isSubtreeLoaded() {
        return childIds.isEmpty() || loadedChildren.size() == childIds.size();
    }

    /**
     * Calculates the total budget of this OU and its entire subtree.
     *
     * @return the total subtree {@link Money} budget
     */
    public Money totalSubtreeBudget() {
        if (loadedChildren.isEmpty()) {
            return budget;
        }
        return loadedChildren.stream()
                .map(OrganizationalUnit::totalSubtreeBudget)
                .reduce(Money.zero(budget.currency()), Money::plus);
    }

    /**
     * Factory method creating a leaf organizational unit (SUBAREA) with no children.
     *
     * @param id        the unique identifier
     * @param name      the unit name
     * @param owners    the set of corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the assigned budget
     * @return a leaf {@link OrganizationalUnit}
     */
    public static OrganizationalUnit leaf(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget) {
        return new OrganizationalUnit(
                id,
                name,
                OuType.SUBAREA,
                owners,
                parentIds,
                budget,
                Set.of(),
                Set.of());
    }

    /**
     * Factory method creating an organizational unit with child IDs only (subtree not loaded).
     *
     * @param id        the unit ID
     * @param name      the unit name
     * @param type      the OU classification type
     * @param owners    the set of owner corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the assigned budget
     * @param childIds  the set of child OU IDs
     * @return an {@link OrganizationalUnit} with unloaded subtree
     */
    public static OrganizationalUnit of(
            OuId id,
            OuName name,
            OuType type,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<OuId> childIds) {
        return new OrganizationalUnit(
                id,
                name,
                type,
                owners,
                parentIds,
                budget,
                childIds,
                Set.of());
    }

    /**
     * Factory method creating an organizational unit with loaded child units (subtree loaded).
     *
     * @param id        the unit ID
     * @param name      the unit name
     * @param type      the OU classification type
     * @param owners    the set of owner corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the assigned budget
     * @param children  the set of child {@link OrganizationalUnit} objects
     * @return an {@link OrganizationalUnit} with loaded subtree
     */
    public static OrganizationalUnit withChildren(
            OuId id,
            OuName name,
            OuType type,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<OrganizationalUnit> children) {
        final var ids = children == null
                ? Set.<OuId>of()
                : children.stream().map(OrganizationalUnit::id).collect(Collectors.toSet());
        final var loaded = children == null ? Set.<OrganizationalUnit>of() : children;
        return new OrganizationalUnit(id, name, type, owners, parentIds, budget, ids, loaded);
    }
}
