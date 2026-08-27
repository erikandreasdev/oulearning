package com.example.oulearning.organization.domain.hierarchy.repository;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

import java.util.List;
import java.util.Optional;

public interface OrganizationalUnitRepository {
    Optional<OrganizationalUnit> findById(OrganizationalUnitId id);

    List<OrganizationalUnit> findSubtreeById(OrganizationalUnitId id);

    void save(OrganizationalUnit organizationalUnit);
}
