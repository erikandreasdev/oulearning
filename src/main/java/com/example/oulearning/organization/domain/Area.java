package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Value object representing an Area (Type 1 Organizational Unit).
 * An Area references child Subareas by their {@link OuId}, and optionally holds {@link Subarea} instances when the subtree is loaded.
 * <p>
 * If child Subareas are loaded, the sum of their budgets must match the Area budget.
 * </p>
 *
 * @param id             the unique identifier of the area
 * @param name           the name of the area
 * @param owners         the set of corporate keys owning/managing this area
 * @param parentIds      the set of parent OU identifiers (0 or more)
 * @param budget         the total assigned budget for this area
 * @param subareaIds     the set of child Subarea IDs (0 or more)
 * @param loadedSubareas the set of loaded child Subareas (empty if subtree is not loaded)
 */
public record Area(
        OuId id,
        OuName name,
        Set<CorporateKey> owners,
        Set<OuId> parentIds,
        Money budget,
        Set<OuId> subareaIds,
        Set<Subarea> loadedSubareas)
        implements OrganizationalUnit {

    /**
     * Compact constructor validating invariants, ensuring immutable sets, and verifying the budget rule when subtree is loaded.
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
        if (subareaIds == null) {
            throw new InvalidOuException("Area subarea IDs cannot be null");
        }
        if (loadedSubareas == null) {
            throw new InvalidOuException("Area loaded subareas cannot be null");
        }

        owners = Set.copyOf(owners);
        parentIds = Set.copyOf(parentIds);
        subareaIds = Set.copyOf(subareaIds);
        loadedSubareas = Set.copyOf(loadedSubareas);

        if (!loadedSubareas.isEmpty()) {
            final var loadedIds =
                    loadedSubareas.stream().map(Subarea::id).collect(Collectors.toSet());
            if (!subareaIds.containsAll(loadedIds)) {
                throw new InvalidOuException("Loaded subareas contain IDs not registered in subareaIds");
            }

            if (loadedSubareas.size() == subareaIds.size()) {
                final var totalSubareasBudget = loadedSubareas.stream()
                        .map(Subarea::budget)
                        .reduce(Money.zero(budget.currency()), Money::plus);

                if (!totalSubareasBudget.equals(budget)) {
                    throw new AreaBudgetMismatchException(
                            "Area '%s' budget (%s) does not match the sum of its subareas' budgets (%s)"
                                    .formatted(name.value(), budget, totalSubareasBudget));
                }
            }
        }
    }

    @Override
    public OuType type() {
        return OuType.AREA;
    }

    /**
     * Checks if all child subareas are loaded in this Area instance.
     *
     * @return {@code true} if subareaIds is empty or all subarea objects are loaded
     */
    public boolean isSubtreeLoaded() {
        return subareaIds.isEmpty() || loadedSubareas.size() == subareaIds.size();
    }

    /**
     * Factory method creating an {@link Area} with child subarea IDs only (subtree not loaded).
     *
     * @param id         the area ID
     * @param name       the area name
     * @param owners     the set of owner corporate keys
     * @param parentIds  the set of parent OU IDs
     * @param budget     the assigned budget
     * @param subareaIds the set of child subarea IDs
     * @return an {@link Area} with unloaded subtree
     */
    public static Area of(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<OuId> subareaIds) {
        return new Area(id, name, owners, parentIds, budget, subareaIds, Set.of());
    }

    /**
     * Factory method creating an {@link Area} with loaded child subarea objects (subtree loaded).
     *
     * @param id        the area ID
     * @param name      the area name
     * @param owners    the set of owner corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the assigned budget
     * @param subareas  the set of child {@link Subarea} objects
     * @return an {@link Area} with loaded subtree
     */
    public static Area withSubareas(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<Subarea> subareas) {
        final var ids = subareas == null
                ? Set.<OuId>of()
                : subareas.stream().map(Subarea::id).collect(Collectors.toSet());
        final var loaded = subareas == null ? Set.<Subarea>of() : subareas;
        return new Area(id, name, owners, parentIds, budget, ids, loaded);
    }
}
