package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case input port for creating an Organizational Unit.
 */
public interface CreateOrganizationalUnitUseCase {
    UUID execute(CreateOrganizationalUnitCommand command);
}
