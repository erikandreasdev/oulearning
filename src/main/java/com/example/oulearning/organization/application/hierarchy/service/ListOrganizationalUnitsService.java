package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.usecase.ListOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListOrganizationalUnitsService implements ListOrganizationalUnitsUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public ListOrganizationalUnitsService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public List<OrganizationalUnit> execute() {
        return organizationalUnitRepository.findAll();
    }
}
