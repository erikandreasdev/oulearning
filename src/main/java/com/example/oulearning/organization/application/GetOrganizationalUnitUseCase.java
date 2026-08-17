package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import java.util.Optional;

/**
 * Use case input port for retrieving an Organizational Unit.
 */
public interface GetOrganizationalUnitUseCase {
    Optional<OrganizationalUnit> execute(GetOrganizationalUnitQuery query);
}
