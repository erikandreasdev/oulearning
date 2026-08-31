package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import java.util.List;

public interface ListOrganizationalUnitsUseCase {
    List<OrganizationalUnit> execute();
}
