package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.Money;
import java.util.Set;

/**
 * Sealed interface representing an organizational unit within the organization hierarchy.
 * Permitted implementations are {@link Area} (Type 1) and {@link Subarea} (Type 2).
 */
public sealed interface OrganizationalUnit permits Area, Subarea {

    /**
     * @return the strongly-typed identifier of the organizational unit
     */
    OuId id();

    /**
     * @return the name of the organizational unit
     */
    OuName name();

    /**
     * @return the set of corporate keys owning/managing this organizational unit
     */
    Set<CorporateKey> owners();

    /**
     * @return the set of parent OU identifiers (0 or more)
     */
    Set<OuId> parentIds();

    /**
     * @return the allocated budget for this organizational unit
     */
    Money budget();

    /**
     * @return the type of organizational unit (AREA or SUBAREA)
     */
    OuType type();
}
