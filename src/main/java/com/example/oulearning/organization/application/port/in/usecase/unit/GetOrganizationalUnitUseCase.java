package com.example.oulearning.organization.application.port.in.usecase.unit;

import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import java.util.Optional;
import com.example.oulearning.organization.application.port.in.query.GetOrganizationalUnitQuery;

/**
 * Use case input port for retrieving an Organizational Unit.
 */
public interface GetOrganizationalUnitUseCase {
    Optional<OrganizationalUnit> execute(GetOrganizationalUnitQuery query);
}
