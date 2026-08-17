package com.example.oulearning.training.infrastructure.web;

import com.example.oulearning.training.application.ApproveTrainingRequestCommand;
import com.example.oulearning.training.application.ApproveTrainingRequestUseCase;
import com.example.oulearning.training.application.GetTrainingRequestQuery;
import com.example.oulearning.training.application.GetTrainingRequestUseCase;
import com.example.oulearning.training.application.GetTrainingRequestsQuery;
import com.example.oulearning.training.application.GetTrainingRequestsUseCase;
import com.example.oulearning.training.application.RejectTrainingRequestCommand;
import com.example.oulearning.training.application.RejectTrainingRequestUseCase;
import com.example.oulearning.training.application.SubmitTrainingRequestCommand;
import com.example.oulearning.training.application.SubmitTrainingRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
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
 * REST Controller for managing Training Requests submitted by OU owners and reviewed by Managers.
 */
@RestController
@RequestMapping("/api/v1/training-requests")
@Tag(name = "Training Requests", description = "Endpoints for submitting, reviewing, approving, rejecting, and querying OU employee training requests")
public class TrainingRequestRestController {

    private final SubmitTrainingRequestUseCase submitUseCase;
    private final ApproveTrainingRequestUseCase approveUseCase;
    private final RejectTrainingRequestUseCase rejectUseCase;
    private final GetTrainingRequestUseCase getUseCase;
    private final GetTrainingRequestsUseCase searchUseCase;

    public TrainingRequestRestController(
            SubmitTrainingRequestUseCase submitUseCase,
            ApproveTrainingRequestUseCase approveUseCase,
            RejectTrainingRequestUseCase rejectUseCase,
            GetTrainingRequestUseCase getUseCase,
            GetTrainingRequestsUseCase searchUseCase) {
        this.submitUseCase = Objects.requireNonNull(submitUseCase, "SubmitTrainingRequestUseCase cannot be null");
        this.approveUseCase = Objects.requireNonNull(approveUseCase, "ApproveTrainingRequestUseCase cannot be null");
        this.rejectUseCase = Objects.requireNonNull(rejectUseCase, "RejectTrainingRequestUseCase cannot be null");
        this.getUseCase = Objects.requireNonNull(getUseCase, "GetTrainingRequestUseCase cannot be null");
        this.searchUseCase = Objects.requireNonNull(searchUseCase, "GetTrainingRequestsUseCase cannot be null");
    }

    @PostMapping
    @Operation(summary = "Submit a training request", description = "Submits a training request on behalf of an OU owner, reserving funds in the OU budget and setting state to DRAFT")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Training request created in DRAFT successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain rule violation (unauthorized requester, invalid assistant, etc.)",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<TrainingRequestResponse> submit(
            @Valid @RequestBody SubmitTrainingRequest request) {
        final var command = new SubmitTrainingRequestCommand(
                request.id(),
                request.ouId(),
                request.requesterCorporateKey(),
                request.name(),
                request.costAmount(),
                request.costCurrency(),
                request.purposeType(),
                request.purposeCustomText(),
                request.trainingHours(),
                request.availableAtOrgUniversity(),
                request.assistantCorporateKeys());

        final var requestId = submitUseCase.execute(command);
        final var created = getUseCase.execute(new GetTrainingRequestQuery(requestId))
                .orElseThrow(() -> new NoSuchElementException("Created training request '%s' not found".formatted(requestId)));

        return ResponseEntity.created(URI.create("/api/v1/training-requests/" + requestId))
                .body(TrainingRequestResponse.fromDomain(created));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve training request", description = "Authorizes and approves a training request by a manager, consuming the reserved OU budget")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Training request approved successfully"),
        @ApiResponse(responseCode = "404", description = "Training request not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain rule violation (unauthorized manager, invalid state transition)",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<TrainingRequestResponse> approve(
            @Parameter(description = "UUID of the training request") @PathVariable UUID id,
            @Valid @RequestBody ApproveTrainingRequest request) {
        final var command = new ApproveTrainingRequestCommand(
                id, request.managerCorporateKey(), request.managerNotes());
        approveUseCase.execute(command);

        final var updated = getUseCase.execute(new GetTrainingRequestQuery(id))
                .orElseThrow(() -> new NoSuchElementException("Training request '%s' not found".formatted(id)));

        return ResponseEntity.ok(TrainingRequestResponse.fromDomain(updated));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject training request", description = "Rejects a training request by a manager with a mandatory rejection reason, releasing the reserved OU budget")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Training request rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Training request not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain rule violation (unauthorized manager, missing reason, invalid state transition)",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<TrainingRequestResponse> reject(
            @Parameter(description = "UUID of the training request") @PathVariable UUID id,
            @Valid @RequestBody RejectTrainingRequest request) {
        final var command = new RejectTrainingRequestCommand(
                id, request.managerCorporateKey(), request.rejectionReason(), request.managerNotes());
        rejectUseCase.execute(command);

        final var updated = getUseCase.execute(new GetTrainingRequestQuery(id))
                .orElseThrow(() -> new NoSuchElementException("Training request '%s' not found".formatted(id)));

        return ResponseEntity.ok(TrainingRequestResponse.fromDomain(updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get training request by ID", description = "Retrieves a training request in full detail by its UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Training request found"),
        @ApiResponse(responseCode = "404", description = "Training request not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<TrainingRequestResponse> getById(
            @Parameter(description = "UUID of the training request") @PathVariable UUID id) {
        final var tr = getUseCase.execute(new GetTrainingRequestQuery(id))
                .orElseThrow(() -> new NoSuchElementException("Training request '%s' not found".formatted(id)));

        return ResponseEntity.ok(TrainingRequestResponse.fromDomain(tr));
    }

    @GetMapping
    @Operation(summary = "Search training requests", description = "Retrieves training requests filtered by OU IDs, OU Names, Status, and Fiscal Year")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of matching training requests")
    })
    public ResponseEntity<List<TrainingRequestResponse>> search(
            @Parameter(description = "Optional single OU UUID") @RequestParam(name = "ouId", required = false) UUID ouId,
            @Parameter(description = "Optional list of OU UUIDs") @RequestParam(name = "ouIds", required = false) List<UUID> ouIds,
            @Parameter(description = "Optional list of OU Names") @RequestParam(name = "ouNames", required = false) List<String> ouNames,
            @Parameter(description = "Optional Status (DRAFT, APPROVED, REJECTED, CANCELLED)") @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "Optional Fiscal Year (e.g. 2026)") @RequestParam(name = "fiscalYear", required = false) Integer fiscalYear) {

        final var allOuIds = new ArrayList<UUID>();
        if (ouId != null) {
            allOuIds.add(ouId);
        }
        if (ouIds != null) {
            allOuIds.addAll(ouIds);
        }

        final var query = GetTrainingRequestsQuery.of(
                allOuIds,
                ouNames != null ? ouNames : List.of(),
                status,
                fiscalYear);

        final var list = searchUseCase.execute(query).stream()
                .map(TrainingRequestResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(list);
    }
}
