package com.example.oulearning.organization.domain;

import java.util.Optional;

/**
 * Domain repository port for searching and persisting organizational units.
 */
public interface OrganizationalUnitRepository {

    /**
     * Finds an Area matching the provided search criteria.
     *
     * @param criteria the search criteria specifying ID, Name, and whether to load the subtree
     * @return an {@link Optional} containing the Area if found, or empty
     */
    Optional<Area> findArea(OuSearchCriteria criteria);

    /**
     * Finds a Subarea matching the provided search criteria.
     *
     * @param criteria the search criteria specifying ID and/or Name
     * @return an {@link Optional} containing the Subarea if found, or empty
     */
    Optional<Subarea> findSubarea(OuSearchCriteria criteria);

    /**
     * Finds any OrganizationalUnit matching the provided search criteria.
     *
     * @param criteria the search criteria
     * @return an {@link Optional} containing the OrganizationalUnit if found, or empty
     */
    Optional<OrganizationalUnit> find(OuSearchCriteria criteria);

    /**
     * Persists or updates an Area aggregate.
     *
     * @param area the Area to save
     */
    void save(Area area);

    /**
     * Persists or updates a Subarea aggregate.
     *
     * @param subarea the Subarea to save
     */
    void save(Subarea subarea);
}
