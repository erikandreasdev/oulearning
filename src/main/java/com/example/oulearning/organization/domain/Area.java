package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import java.util.Set;

/**
 * Value object representing an Area (Type 1 Organizational Unit).
 * An Area can contain 0 or more child Subareas.
 * <p>
 * If the Area contains subareas, the sum of the budgets of all child Subareas must match the Area budget.
 * </p>
 *
 * @param id        the unique identifier of the area
 * @param name      the name of the area
 * @param owners    the set of corporate keys owning/managing this area
 * @param parentIds the set of parent OU identifiers (0 or more)
 * @param budget    the total assigned budget for this area
 * @param subareas  the set of child Subareas (0 or more)
 */
public record Area(
        OuId id,
        OuName name,
        Set<CorporateKey> owners,
        Set<OuId> parentIds,
        Money budget,
        Set<Subarea> subareas)
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
        if (subareas == null) {
            throw new InvalidOuException("Area subareas cannot be null");
        }

        owners = Set.copyOf(owners);
        parentIds = Set.copyOf(parentIds);
        subareas = Set.copyOf(subareas);

        if (!subareas.isEmpty()) {
            final var totalSubareasBudget = subareas.stream()
                    .map(Subarea::budget)
                    .reduce(Money.zero(budget.currency()), Money::plus);

            if (!totalSubareasBudget.equals(budget)) {
                throw new AreaBudgetMismatchException(
                        "Area '%s' budget (%s) does not match the sum of its subareas' budgets (%s)"
                                .formatted(name.value(), budget, totalSubareasBudget));
            }
        }
    }

    @Override
    public OuType type() {
        return OuType.AREA;
    }

    /**
     * Factory method to create an {@link Area}.
     *
     * @param id        the unique identifier
     * @param name      the name
     * @param owners    the set of corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the budget
     * @param subareas  the set of child Subareas
     * @return a validated {@link Area}
     */
    public static Area of(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget,
            Set<Subarea> subareas) {
        return new Area(id, name, owners, parentIds, budget, subareas);
    }
}
