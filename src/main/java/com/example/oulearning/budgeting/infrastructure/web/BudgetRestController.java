package com.example.oulearning.budgeting.infrastructure.web;

import com.example.oulearning.budgeting.application.AllocateBudgetCommand;
import com.example.oulearning.budgeting.application.AllocateBudgetUseCase;
import com.example.oulearning.budgeting.application.ConsumeBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.ConsumeFundsCommand;
import com.example.oulearning.budgeting.application.DistributeBudgetCommand;
import com.example.oulearning.budgeting.application.DistributeBudgetUseCase;
import com.example.oulearning.budgeting.application.GetBudgetQuery;
import com.example.oulearning.budgeting.application.GetBudgetUseCase;
import com.example.oulearning.budgeting.application.ReleaseBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.ReleaseFundsCommand;
import com.example.oulearning.budgeting.application.ReserveBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.ReserveFundsCommand;
import com.example.oulearning.budgeting.application.SpendDirectBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.SpendDirectCommand;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Budgets and fund operations.
 */
@RestController
@RequestMapping("/api/v1/budgets")
@Tag(name = "Budgets", description = "Endpoints for allocating budgets, fund lifecycle operations, and multi-strategy distribution")
public class BudgetRestController {

    private final AllocateBudgetUseCase allocateUseCase;
    private final GetBudgetUseCase getUseCase;
    private final ReserveBudgetFundsUseCase reserveUseCase;
    private final ReleaseBudgetFundsUseCase releaseUseCase;
    private final ConsumeBudgetFundsUseCase consumeUseCase;
    private final SpendDirectBudgetFundsUseCase spendDirectUseCase;
    private final DistributeBudgetUseCase distributeUseCase;

    public BudgetRestController(
            AllocateBudgetUseCase allocateUseCase,
            GetBudgetUseCase getUseCase,
            ReserveBudgetFundsUseCase reserveUseCase,
            ReleaseBudgetFundsUseCase releaseUseCase,
            ConsumeBudgetFundsUseCase consumeUseCase,
            SpendDirectBudgetFundsUseCase spendDirectUseCase,
            DistributeBudgetUseCase distributeUseCase) {
        this.allocateUseCase = Objects.requireNonNull(allocateUseCase, "AllocateBudgetUseCase cannot be null");
        this.getUseCase = Objects.requireNonNull(getUseCase, "GetBudgetUseCase cannot be null");
        this.reserveUseCase = Objects.requireNonNull(reserveUseCase, "ReserveBudgetFundsUseCase cannot be null");
        this.releaseUseCase = Objects.requireNonNull(releaseUseCase, "ReleaseBudgetFundsUseCase cannot be null");
        this.consumeUseCase = Objects.requireNonNull(consumeUseCase, "ConsumeBudgetFundsUseCase cannot be null");
        this.spendDirectUseCase = Objects.requireNonNull(spendDirectUseCase, "SpendDirectBudgetFundsUseCase cannot be null");
        this.distributeUseCase = Objects.requireNonNull(distributeUseCase, "DistributeBudgetUseCase cannot be null");
    }

    @PostMapping
    @Operation(summary = "Allocate initial budget", description = "Allocates a new budget to an organizational unit")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Budget allocated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain rule violation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> allocate(@Valid @RequestBody AllocateBudgetRequest request) {
        final var command = new AllocateBudgetCommand(
                request.budgetId(),
                request.ouId(),
                request.amount(),
                request.currencyCode());

        final var budgetId = allocateUseCase.execute(command);
        final var budget = getUseCase.execute(GetBudgetQuery.byBudgetId(budgetId))
                .orElseThrow(() -> new NoSuchElementException("Allocated budget '%s' not found".formatted(budgetId)));

        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId)).body(BudgetResponse.fromDomain(budget));
    }

    @GetMapping("/{budgetId}")
    @Operation(summary = "Get budget by ID", description = "Retrieves a budget by its UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget found"),
        @ApiResponse(responseCode = "404", description = "Budget not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> getById(
            @Parameter(description = "UUID of the budget") @PathVariable UUID budgetId) {
        final var budget = getUseCase.execute(GetBudgetQuery.byBudgetId(budgetId))
                .orElseThrow(() -> new NoSuchElementException("Budget '%s' not found".formatted(budgetId)));

        return ResponseEntity.ok(BudgetResponse.fromDomain(budget));
    }

    @GetMapping("/ou/{ouId}")
    @Operation(summary = "Get budget by OU ID", description = "Retrieves the budget assigned to a specific organizational unit")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget found"),
        @ApiResponse(responseCode = "404", description = "Budget not found for given OU",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> getByOuId(
            @Parameter(description = "UUID of the organizational unit") @PathVariable UUID ouId) {
        final var budget = getUseCase.execute(GetBudgetQuery.byOuId(ouId))
                .orElseThrow(() -> new NoSuchElementException("Budget for OU '%s' not found".formatted(ouId)));

        return ResponseEntity.ok(BudgetResponse.fromDomain(budget));
    }

    @PostMapping("/{budgetId}/reserve")
    @Operation(summary = "Reserve funds", description = "Reserves funds from the available budget")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Funds reserved successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Insufficient available funds",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> reserve(
            @PathVariable UUID budgetId,
            @Valid @RequestBody FundOperationRequest request) {
        final var command = new ReserveFundsCommand(budgetId, request.amount(), request.currencyCode());
        final var updated = reserveUseCase.execute(command);
        return ResponseEntity.ok(BudgetResponse.fromDomain(updated));
    }

    @PostMapping("/{budgetId}/release")
    @Operation(summary = "Release reserved funds", description = "Releases previously reserved funds back to available budget")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Funds released successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Insufficient reserved funds",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> release(
            @PathVariable UUID budgetId,
            @Valid @RequestBody FundOperationRequest request) {
        final var command = new ReleaseFundsCommand(budgetId, request.amount(), request.currencyCode());
        final var updated = releaseUseCase.execute(command);
        return ResponseEntity.ok(BudgetResponse.fromDomain(updated));
    }

    @PostMapping("/{budgetId}/consume")
    @Operation(summary = "Consume reserved funds", description = "Consumes reserved funds into spent budget")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Funds consumed successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Insufficient reserved funds",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> consume(
            @PathVariable UUID budgetId,
            @Valid @RequestBody FundOperationRequest request) {
        final var command = new ConsumeFundsCommand(budgetId, request.amount(), request.currencyCode());
        final var updated = consumeUseCase.execute(command);
        return ResponseEntity.ok(BudgetResponse.fromDomain(updated));
    }

    @PostMapping("/{budgetId}/spend-direct")
    @Operation(summary = "Direct spend from budget", description = "Spends directly from available budget without prior reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Direct spend completed successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Insufficient available funds",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetResponse> spendDirect(
            @PathVariable UUID budgetId,
            @Valid @RequestBody FundOperationRequest request) {
        final var command = new SpendDirectCommand(budgetId, request.amount(), request.currencyCode());
        final var updated = spendDirectUseCase.execute(command);
        return ResponseEntity.ok(BudgetResponse.fromDomain(updated));
    }

    @PostMapping("/distribute")
    @Operation(summary = "Distribute budget to child OUs", description = "Distributes parent OU budget among child OUs using EXCLUSIVE, EQUAL, or EXPLICIT strategy")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget distributed successfully"),
        @ApiResponse(responseCode = "404", description = "Parent budget not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Distribution rule violation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<BudgetDistributionResultResponse> distribute(
            @Valid @RequestBody DistributeBudgetRequest request) {
        final var command = new DistributeBudgetCommand(
                request.parentOuId(),
                request.strategyType(),
                request.childOuIds(),
                request.explicitAllocations(),
                request.currencyCode());

        final var result = distributeUseCase.execute(command);
        return ResponseEntity.ok(BudgetDistributionResultResponse.fromDomain(result));
    }
}
