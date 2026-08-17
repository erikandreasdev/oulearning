package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.application.CreateOrganizationSnapshotCommand;
import com.example.oulearning.organization.application.CreateOrganizationSnapshotUseCase;
import com.example.oulearning.organization.application.GetLatestOrganizationUseCase;
import com.example.oulearning.organization.application.GetOrganizationHistoryUseCase;
import com.example.oulearning.organization.application.GetOrganizationSnapshotQuery;
import com.example.oulearning.organization.application.GetOrganizationSnapshotUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.List;
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

/**
 * REST Controller for managing Organization hierarchy snapshots and historical queries.
 */
@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organization Snapshots", description = "Endpoints for taking organization snapshots, caching, and time-travel history")
public class OrganizationSnapshotRestController {

    private final CreateOrganizationSnapshotUseCase createSnapshotUseCase;
    private final GetLatestOrganizationUseCase getLatestUseCase;
    private final GetOrganizationSnapshotUseCase getSnapshotUseCase;
    private final GetOrganizationHistoryUseCase getHistoryUseCase;

    public OrganizationSnapshotRestController(
            CreateOrganizationSnapshotUseCase createSnapshotUseCase,
            GetLatestOrganizationUseCase getLatestUseCase,
            GetOrganizationSnapshotUseCase getSnapshotUseCase,
            GetOrganizationHistoryUseCase getHistoryUseCase) {
        this.createSnapshotUseCase = Objects.requireNonNull(createSnapshotUseCase, "CreateOrganizationSnapshotUseCase cannot be null");
        this.getLatestUseCase = Objects.requireNonNull(getLatestUseCase, "GetLatestOrganizationUseCase cannot be null");
        this.getSnapshotUseCase = Objects.requireNonNull(getSnapshotUseCase, "GetOrganizationSnapshotUseCase cannot be null");
        this.getHistoryUseCase = Objects.requireNonNull(getHistoryUseCase, "GetOrganizationHistoryUseCase cannot be null");
    }

    @PostMapping("/snapshots")
    @Operation(summary = "Take an organization snapshot", description = "Persists a full snapshot of the organization hierarchy starting from the root OU")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Snapshot created and cached"),
        @ApiResponse(responseCode = "404", description = "Root OU not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain rule violation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationSnapshotResponse> createSnapshot(
            @Valid @RequestBody CreateSnapshotRequest request) {
        final var command = new CreateOrganizationSnapshotCommand(
                request.snapshotId(),
                request.rootOuId(),
                request.createdAt());

        final var snapshotId = createSnapshotUseCase.execute(command);
        final var snapshot = getSnapshotUseCase.execute(GetOrganizationSnapshotQuery.byId(snapshotId))
                .orElseThrow(() -> new NoSuchElementException("Created snapshot '%s' not found".formatted(snapshotId)));

        final var response = OrganizationSnapshotResponse.fromDomain(snapshot);
        return ResponseEntity.created(URI.create("/api/v1/organizations/snapshots/" + snapshotId)).body(response);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest organization snapshot", description = "Retrieves the latest organization snapshot (served directly from memory cache)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Latest snapshot returned"),
        @ApiResponse(responseCode = "404", description = "No organization snapshots found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationSnapshotResponse> getLatest() {
        final var organization = getLatestUseCase.execute()
                .orElseThrow(() -> new NoSuchElementException("No organization snapshots found"));

        return ResponseEntity.ok(OrganizationSnapshotResponse.fromDomain(organization));
    }

    @GetMapping("/snapshots/{snapshotId}")
    @Operation(summary = "Get snapshot by ID", description = "Retrieves a specific organization snapshot by its UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Snapshot found"),
        @ApiResponse(responseCode = "404", description = "Snapshot not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationSnapshotResponse> getBySnapshotId(
            @Parameter(description = "UUID of the snapshot") @PathVariable UUID snapshotId) {
        final var organization = getSnapshotUseCase.execute(GetOrganizationSnapshotQuery.byId(snapshotId))
                .orElseThrow(() -> new NoSuchElementException("Snapshot '%s' not found".formatted(snapshotId)));

        return ResponseEntity.ok(OrganizationSnapshotResponse.fromDomain(organization));
    }

    @GetMapping("/snapshots")
    @Operation(summary = "Query snapshot at historical timestamp", description = "Point-in-time time-travel query retrieving the active snapshot at the given timestamp")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Snapshot found at timestamp"),
        @ApiResponse(responseCode = "404", description = "No snapshot found at timestamp",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<OrganizationSnapshotResponse> getSnapshotAt(
            @Parameter(description = "ISO-8601 timestamp (e.g. 2026-08-17T12:00:00Z)") @RequestParam Instant at) {
        final var organization = getSnapshotUseCase.execute(GetOrganizationSnapshotQuery.at(at))
                .orElseThrow(() -> new NoSuchElementException("No snapshot found at timestamp '%s'".formatted(at)));

        return ResponseEntity.ok(OrganizationSnapshotResponse.fromDomain(organization));
    }

    @GetMapping("/snapshots/history")
    @Operation(summary = "Get snapshot history", description = "Retrieves all organization snapshots ordered chronologically")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chronological snapshot history returned")
    })
    public ResponseEntity<List<OrganizationSnapshotResponse>> getHistory() {
        final var history = getHistoryUseCase.execute().stream()
                .map(OrganizationSnapshotResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(history);
    }
}
