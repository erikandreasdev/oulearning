package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import java.util.Set;

/**
 * Value object representing a Subarea (Type 2 Organizational Unit).
 * Subareas are leaf organizational units with no child OUs.
 *
 * @param id        the unique identifier of the subarea
 * @param name      the name of the subarea
 * @param owners    the set of corporate keys owning/managing this subarea
 * @param parentIds the set of parent OU identifiers (0 or more)
 * @param budget    the assigned budget for this subarea
 */
public record Subarea(
        OuId id,
        OuName name,
        Set<CorporateKey> owners,
        Set<OuId> parentIds,
        Money budget)
        implements OrganizationalUnit {

    /**
     * Compact constructor validating invariants and ensuring immutable sets.
     */
    public Subarea {
        if (id == null) {
            throw new InvalidOuException("Subarea ID cannot be null");
        }
        if (name == null) {
            throw new InvalidOuException("Subarea name cannot be null");
        }
        if (owners == null) {
            throw new InvalidOuException("Subarea owners cannot be null");
        }
        if (parentIds == null) {
            throw new InvalidOuException("Subarea parent IDs cannot be null");
        }
        if (budget == null) {
            throw new InvalidOuException("Subarea budget cannot be null");
        }

        owners = Set.copyOf(owners);
        parentIds = Set.copyOf(parentIds);
    }

    @Override
    public OuType type() {
        return OuType.SUBAREA;
    }

    @Override
    public Set<OuId> childIds() {
        return Set.of();
    }

    @Override
    public Money totalSubtreeBudget() {
        return budget;
    }

    /**
     * Factory method to create a {@link Subarea}.
     *
     * @param id        the unique identifier
     * @param name      the name
     * @param owners    the set of corporate keys
     * @param parentIds the set of parent OU IDs
     * @param budget    the budget
     * @return a validated {@link Subarea}
     */
    public static Subarea of(
            OuId id,
            OuName name,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Money budget) {
        return new Subarea(id, name, owners, parentIds, budget);
    }
}
