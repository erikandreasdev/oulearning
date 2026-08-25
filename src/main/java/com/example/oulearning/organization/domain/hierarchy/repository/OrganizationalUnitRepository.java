package com.example.oulearning.organization.domain.hierarchy.repository;

import com.example.oulearning.organization.domain.hierarchy.model.*;

import java.util.Optional;

public interface OrganizationalUnitRepository {
    Optional<OrganizationalUnit> findById(OrganizationalUnitId id);

    void save(OrganizationalUnit organizationalUnit);
}
