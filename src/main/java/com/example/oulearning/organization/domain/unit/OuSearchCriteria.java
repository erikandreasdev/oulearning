package com.example.oulearning.organization.domain.unit;

import com.example.oulearning.organization.domain.unit.exception.InvalidOuException;
import java.util.Optional;

/**
 * Value object encapsulating search parameters for finding organizational units.
 * Supports searching by {@link OuId}, {@link OuName}, or both, with a flag to include the loaded subtree.
 *
 * @param id             the optional {@link OuId} (may be null if searching by name)
 * @param name           the optional {@link OuName} (may be null if searching by id)
 * @param includeSubtree whether to fetch/load child subareas for an Area
 */
public record OuSearchCriteria(OuId id, OuName name, boolean includeSubtree) {

    public OuSearchCriteria {
        if (id == null && name == null) {
            throw new InvalidOuException("At least one search key (id or name) must be provided in OuSearchCriteria");
        }
    }

    public Optional<OuId> findId() {
        return Optional.ofNullable(id);
    }

    public Optional<OuName> findName() {
        return Optional.ofNullable(name);
    }

    public static OuSearchCriteria byId(OuId id, boolean includeSubtree) {
        if (id == null) {
            throw new InvalidOuException("OuId cannot be null for byId search criteria");
        }
        return new OuSearchCriteria(id, null, includeSubtree);
    }

    public static OuSearchCriteria byId(OuId id) {
        return byId(id, false);
    }

    public static OuSearchCriteria byName(OuName name, boolean includeSubtree) {
        if (name == null) {
            throw new InvalidOuException("OuName cannot be null for byName search criteria");
        }
        return new OuSearchCriteria(null, name, includeSubtree);
    }

    public static OuSearchCriteria byName(OuName name) {
        return byName(name, false);
    }

    public static OuSearchCriteria of(OuId id, OuName name, boolean includeSubtree) {
        return new OuSearchCriteria(id, name, includeSubtree);
    }
}
