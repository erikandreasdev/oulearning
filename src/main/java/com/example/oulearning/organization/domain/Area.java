package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Value object representing an Area (Type 1 Organizational Unit).
 * An Area can contain child OrganizationalUnits (child Areas or child Subareas) across N hierarchy levels.
 * <p>
 * If child OUs are loaded, the sum of their budgets must match the Area budget.
 * </p>
 *
 * @param id             the unique identifier of the area
 * @param name           the name of the area
 * @param owners         the set of corporate keys owning/managing this area
 * @param parentIds      the set of parent OU identifiers (0 or more; empty if root OU)
 * @param budget         the total assigned budget for this area
 * @param childIds       the set of child OU IDs (0 or more)
 * @param loadedChildren the set of loaded child {@link OrganizationalUnit}s (empty if subtree is not loaded)
 */
public record Area(
        OuId id,
        OuName name,
        Set<CorporateKey> owners,
        Set<OuId> parentIds,
        Money budget,
        Set<OuId> childIds,
        Set<OrganizationalUnit> loadedChildren)
        implements OrganizationalUnit {

    /**
     * Compact constructor validating invariants, ensuring immutable sets, and verifying the budget rule.
     */
    public Area {
        if (id == null) {
            throw new InvalidOuException("Area ID cannot be null");
        }
        if (name == null) {
            throw new InvalidOuException("Area name cannot be null");
        }
        if (owners == null) {
            throw new InvalidOuException("Area owners cannot be null");
        }
        if (parentIds == null) {
            throw new InvalidOuException("Area parent IDs cannot be null");
        }
        if (budget == null) {
            throw new InvalidOuException("Area budget cannot be null");
        }
        if (childIds == null) {
            throw new InvalidOuException("Area child IDs cannot be null");
        }
        if (loadedChildren == null) {
            throw new InvalidOuException("Area loaded children cannot be null");
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
                    throw new AreaBudgetMismatchException(
                            "Area '%s' budget (%s) does not match the sum of its child OU budgets (%s)"
                                    .formatted(name.value(), budget, totalChildrenBudget));
                }
            }
        }
    }

    @Override
    public OuType type() {
        return OuType.AREA;
    }

    /**
     * Checks if all child OUs are loaded in this Area instance.
     *
     * @return {@code true} if childIds is empty or all child OU objects are loaded
     */
    public boolean isSubtreeLoaded() {
        return childIds.isEmpty() || loadedChildren.size() == childIds.size();
    }

    @Override
    public Money totalSubtreeBudget() {
        if (loadedChildren.isEmpty()) {
            return budget;
        }
        return loadedChildren.stream()
                .map(OrganizationalUnit::totalSubtreeBudget)
                .reduce(Money.zero(budget.currency()), Money::plus);
    }

    /**
     * Factory method creating an {@link Area} with child IDs only (subtree not loaded).
     *
     * @param id        the area ID
     * @param name      the area name
     * @param owners    the set of owner corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the assigned budget
     * @param childIds  the set of child OU IDs
     * @return an {@link Area} with unloaded subtree
     */
    public static Area of(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<OuId> childIds) {
        return new Area(id, name, owners, parentIds, budget, childIds, Set.of());
    }

    /**
     * Factory method creating an {@link Area} with loaded child OU objects (subtree loaded).
     *
     * @param id        the area ID
     * @param name      the area name
     * @param owners    the set of owner corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the assigned budget
     * @param children  the set of child {@link OrganizationalUnit} objects
     * @return an {@link Area} with loaded subtree
     */
    public static Area withChildren(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<OrganizationalUnit> children) {
        final var ids = children == null
                ? Set.<OuId>of()
                : children.stream().map(OrganizationalUnit::id).collect(Collectors.toSet());
        final var loaded = children == null ? Set.<OrganizationalUnit>of() : children;
        return new Area(id, name, owners, parentIds, budget, ids, loaded);
    }
}
