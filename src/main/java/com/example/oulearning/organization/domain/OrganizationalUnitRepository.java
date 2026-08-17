package com.example.oulearning.organization.domain;

import java.util.Optional;

/**
 * Domain repository port for searching and persisting organizational units.
 */
public interface OrganizationalUnitRepository {

    /**
     * Finds an OrganizationalUnit matching the provided search criteria.
     *
     * @param criteria the search criteria specifying ID, Name, and whether to load the subtree
     * @return an {@link Optional} containing the OrganizationalUnit if found, or empty
     */
    Optional<OrganizationalUnit> find(OuSearchCriteria criteria);

    /**
     * Persists or updates an OrganizationalUnit.
     *
     * @param unit the OrganizationalUnit to save
     */
    void save(OrganizationalUnit unit);
}
