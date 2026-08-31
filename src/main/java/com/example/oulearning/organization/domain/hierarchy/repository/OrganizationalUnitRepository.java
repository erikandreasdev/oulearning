package com.example.oulearning.organization.domain.hierarchy.repository;

import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

import java.util.List;
import java.util.Optional;

public interface OrganizationalUnitRepository {
    Optional<OrganizationalUnit> findById(OrganizationalUnitId id);

    Optional<OrganizationalUnit> findByNameAndParentId(Name name, Optional<OrganizationalUnitId> parentId);

    List<OrganizationalUnit> findSubtreeById(OrganizationalUnitId id);

    List<OrganizationalUnit> findAll();

    void save(OrganizationalUnit organizationalUnit);
}
