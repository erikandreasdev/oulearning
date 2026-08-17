package com.example.oulearning.organization.domain;

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

    /**
     * Compact constructor enforcing that at least one search key (id or name) is provided.
     */
    public OuSearchCriteria {
        if (id == null && name == null) {
            throw new InvalidOuException("At least one search key (id or name) must be provided in OuSearchCriteria");
        }
    }

    /**
     * @return an {@link Optional} containing the search {@link OuId} if present
     */
    public Optional<OuId> findId() {
        return Optional.ofNullable(id);
    }

    /**
     * @return an {@link Optional} containing the search {@link OuName} if present
     */
    public Optional<OuName> findName() {
        return Optional.ofNullable(name);
    }

    /**
     * Creates search criteria by {@link OuId} with specified subtree loading flag.
     *
     * @param id             the {@link OuId} to search for
     * @param includeSubtree whether to load the subtree
     * @return the search criteria
     */
    public static OuSearchCriteria byId(OuId id, boolean includeSubtree) {
        if (id == null) {
            throw new InvalidOuException("OuId cannot be null for byId search criteria");
        }
        return new OuSearchCriteria(id, null, includeSubtree);
    }

    /**
     * Creates search criteria by {@link OuId} without loading the subtree.
     *
     * @param id the {@link OuId} to search for
     * @return the search criteria
     */
    public static OuSearchCriteria byId(OuId id) {
        return byId(id, false);
    }

    /**
     * Creates search criteria by {@link OuName} with specified subtree loading flag.
     *
     * @param name           the {@link OuName} to search for
     * @param includeSubtree whether to load the subtree
     * @return the search criteria
     */
    public static OuSearchCriteria byName(OuName name, boolean includeSubtree) {
        if (name == null) {
            throw new InvalidOuException("OuName cannot be null for byName search criteria");
        }
        return new OuSearchCriteria(null, name, includeSubtree);
    }

    /**
     * Creates search criteria by {@link OuName} without loading the subtree.
     *
     * @param name the {@link OuName} to search for
     * @return the search criteria
     */
    public static OuSearchCriteria byName(OuName name) {
        return byName(name, false);
    }

    /**
     * Creates search criteria with both {@link OuId} and {@link OuName}.
     *
     * @param id             the {@link OuId}
     * @param name           the {@link OuName}
     * @param includeSubtree whether to load the subtree
     * @return the search criteria
     */
    public static OuSearchCriteria of(OuId id, OuName name, boolean includeSubtree) {
        return new OuSearchCriteria(id, name, includeSubtree);
    }
}
