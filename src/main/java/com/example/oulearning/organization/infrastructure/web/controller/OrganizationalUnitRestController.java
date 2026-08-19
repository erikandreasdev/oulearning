package com.example.oulearning.organization.infrastructure.web.controller;

import com.example.oulearning.organization.application.port.in.command.CreateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.port.in.usecase.unit.CreateOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.port.in.query.GetOrganizationalUnitQuery;
import com.example.oulearning.organization.application.port.in.usecase.unit.GetOrganizationalUnitUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.oulearning.organization.infrastructure.web.request.CreateOrganizationalUnitRequest;
import com.example.oulearning.organization.infrastructure.web.response.OrganizationalUnitResponse;

/**
 * REST Controller for managing Organizational Units.
 */
@RestController
@RequestMapping("/api/v1/organizational-units")
@Tag(name = "Organizational Units", description = "Endpoints for creating and querying organizational units")
public class OrganizationalUnitRestController {

    private final CreateOrganizationalUnitUseCase createUseCase;
    private final GetOrganizationalUnitUseCase getUseCase;

    public OrganizationalUnitRestController(
            CreateOrganizationalUnitUseCase createUseCase,
            GetOrganizationalUnitUseCase getUseCase) {
        this.createUseCase = Objects.requireNonNull(createUseCase, "CreateOrganizationalUnitUseCase cannot be null");
        this.getUseCase = Objects.requireNonNull(getUseCase, "GetOrganizationalUnitUseCase cannot be null");
    }

    @PostMapping
    @Operation(summary = "Create an organizational unit", description = "Creates a new leaf or composite organizational unit")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Organizational unit created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain rule violation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationalUnitResponse> createUnit(
            @Valid @RequestBody CreateOrganizationalUnitRequest request) {
        final var command = new CreateOrganizationalUnitCommand(
                request.id(),
                request.name(),
                request.ouType(),
                request.ownerCorporateKeys(),
                request.parentId(),
                request.childIds());

        final var createdId = createUseCase.execute(command);
        final var unit = getUseCase.execute(GetOrganizationalUnitQuery.byId(createdId, true))
                .orElseThrow(() -> new NoSuchElementException("Created unit '%s' not found".formatted(createdId)));

        final var response = OrganizationalUnitResponse.fromDomain(unit);
        return ResponseEntity.created(URI.create("/api/v1/organizational-units/" + createdId)).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organizational unit by ID", description = "Retrieves an organizational unit, optionally hydrating its entire subtree")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organizational unit found"),
        @ApiResponse(responseCode = "404", description = "Organizational unit not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationalUnitResponse> getById(
            @Parameter(description = "UUID of the organizational unit") @PathVariable UUID id,
            @Parameter(description = "Whether to recursively load child subtree units")
            @RequestParam(defaultValue = "false") boolean includeSubtree) {

        final var unit = getUseCase.execute(GetOrganizationalUnitQuery.byId(id, includeSubtree))
                .orElseThrow(() -> new NoSuchElementException("Organizational unit '%s' not found".formatted(id)));

        return ResponseEntity.ok(OrganizationalUnitResponse.fromDomain(unit));
    }

    @GetMapping
    @Operation(summary = "Get organizational unit by name", description = "Retrieves an organizational unit by its unique name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organizational unit found"),
        @ApiResponse(responseCode = "404", description = "Organizational unit not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationalUnitResponse> getByName(
            @Parameter(description = "Name of the organizational unit") @RequestParam String name) {

        final var unit = getUseCase.execute(GetOrganizationalUnitQuery.byName(name))
                .orElseThrow(() -> new NoSuchElementException("Organizational unit with name '%s' not found".formatted(name)));

        return ResponseEntity.ok(OrganizationalUnitResponse.fromDomain(unit));
    }
}
