package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class GetOrganizationalUnitService implements GetOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public GetOrganizationalUnitService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public OrganizationalUnit execute(final OrganizationalUnitId id) {
        return organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(id));
    }
}
