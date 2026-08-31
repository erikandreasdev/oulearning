package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationImportException;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.ImportOrganizationUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.ListOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.infrastructure.web.dto.EmployeeResponseDto;
import com.example.oulearning.organization.infrastructure.web.dto.ImportOrganizationResponseDto;
import com.example.oulearning.organization.infrastructure.web.dto.OrganizationalUnitResponseDto;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/organization")
class OrganizationController {

    private final ImportOrganizationUseCase importOrganizationUseCase;
    private final ListOrganizationalUnitsUseCase listOrganizationalUnitsUseCase;

    OrganizationController(
            final ImportOrganizationUseCase importOrganizationUseCase,
            final ListOrganizationalUnitsUseCase listOrganizationalUnitsUseCase) {
        this.importOrganizationUseCase = importOrganizationUseCase;
        this.listOrganizationalUnitsUseCase = listOrganizationalUnitsUseCase;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportOrganizationResponseDto> importOrganization(
            @RequestParam("file") final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new OrganizationImportException("File is missing or empty");
        }

        try (var inputStream = file.getInputStream()) {
            final var result = importOrganizationUseCase.execute(inputStream);
            final var employeeDtos = result.employees().stream()
                    .map(this::toEmployeeResponse)
                    .toList();
            final var unitDtos = result.organizationalUnits().stream()
                    .map(this::toResponse)
                    .toList();
            final var response = new ImportOrganizationResponseDto(
                    result.employeesProcessed(),
                    result.organizationalUnitsProcessed(),
                    result.ownersAssigned(),
                    result.membersAssigned(),
                    employeeDtos,
                    unitDtos);
            return ResponseEntity.ok(response);
        } catch (final IOException ex) {
            throw new OrganizationImportException("Failed to read uploaded file: %s".formatted(ex.getMessage()), ex);
        }
    }

    @GetMapping("/units")
    public ResponseEntity<List<OrganizationalUnitResponseDto>> listOrganizationalUnits() {
        final var units = listOrganizationalUnitsUseCase.execute();
        final var response = units.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private OrganizationalUnitResponseDto toResponse(final OrganizationalUnit ou) {
        final var childIds = ou.childIds().stream()
                .map(OrganizationalUnitId::value)
                .collect(Collectors.toSet());
        final var owners = ou.owners().stream()
                .map(EmployeeId::value)
                .collect(Collectors.toSet());
        final var members = ou.members().stream()
                .map(EmployeeId::value)
                .collect(Collectors.toSet());
        final var parentId = ou.parentId().map(OrganizationalUnitId::value).orElse(null);

        return new OrganizationalUnitResponseDto(
                ou.id().value(),
                ou.name().value(),
                parentId,
                childIds,
                owners,
                members,
                ou.active());
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private EmployeeResponseDto toEmployeeResponse(final Employee employee) {
        return new EmployeeResponseDto(
                employee.id().value(),
                employee.fullName().name().value(),
                employee.fullName().surname().value(),
                employee.email().value(),
                employee.active());
    }
}
