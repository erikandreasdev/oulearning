package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.application.GetEmployeeQuery;
import com.example.oulearning.organization.application.GetEmployeeUseCase;
import com.example.oulearning.organization.application.GetEmployeesByOuQuery;
import com.example.oulearning.organization.application.GetEmployeesByOuUseCase;
import com.example.oulearning.organization.application.RegisterEmployeeCommand;
import com.example.oulearning.organization.application.RegisterEmployeeUseCase;
import com.example.oulearning.organization.application.UploadEmployeesCommand;
import com.example.oulearning.organization.application.UploadEmployeesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for managing Employees, batch file uploads, and querying OU memberships.
 */
@RestController
@RequestMapping(path = "/api/v1/employees", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Employees", description = "Endpoints for employee registration, file uploads, and organizational unit membership lookups")
public class EmployeeRestController {

    private final RegisterEmployeeUseCase registerEmployeeUseCase;
    private final UploadEmployeesUseCase uploadEmployeesUseCase;
    private final GetEmployeeUseCase getEmployeeUseCase;
    private final GetEmployeesByOuUseCase getEmployeesByOuUseCase;

    public EmployeeRestController(
            RegisterEmployeeUseCase registerEmployeeUseCase,
            UploadEmployeesUseCase uploadEmployeesUseCase,
            GetEmployeeUseCase getEmployeeUseCase,
            GetEmployeesByOuUseCase getEmployeesByOuUseCase) {
        this.registerEmployeeUseCase = Objects.requireNonNull(registerEmployeeUseCase, "RegisterEmployeeUseCase cannot be null");
        this.uploadEmployeesUseCase = Objects.requireNonNull(uploadEmployeesUseCase, "UploadEmployeesUseCase cannot be null");
        this.getEmployeeUseCase = Objects.requireNonNull(getEmployeeUseCase, "GetEmployeeUseCase cannot be null");
        this.getEmployeesByOuUseCase = Objects.requireNonNull(getEmployeesByOuUseCase, "GetEmployeesByOuUseCase cannot be null");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new employee and assign to an Organizational Unit")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Employee successfully registered and assigned",
                headers = @Header(name = "Location", description = "URI of the newly registered employee")),
        @ApiResponse(
                responseCode = "400",
                description = "Validation failure in request payload",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Target organizational unit not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
                responseCode = "422",
                description = "Domain invariant violation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request) {
        final var command = new RegisterEmployeeCommand(
                request.corporateKey(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.role(),
                request.ouId());

        final var corporateKey = registerEmployeeUseCase.execute(command);
        final var location = URI.create("/api/v1/employees/" + corporateKey);

        return ResponseEntity.created(location).build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and assign employees from CSV or Excel file",
               description = "Parses an employee list and links them to the active organization snapshot's units")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employees successfully uploaded and assigned"),
        @ApiResponse(responseCode = "400", description = "Invalid file or parameters",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Domain validation failed",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Map<String, Object>> uploadEmployees(
            @Parameter(description = "CSV or Excel file containing the employee list")
            @RequestPart("file") MultipartFile employeeFile,

            @Parameter(description = "Corporate key of the authorized manager performing the upload")
            @RequestParam(value = "managerCorporateKey", required = false) String managerCorporateKey) throws IOException {

        final var empBytes = employeeFile.getBytes();
        final var filename = employeeFile.getOriginalFilename() != null ? employeeFile.getOriginalFilename() : "employees.csv";

        final var command = new UploadEmployeesCommand(managerCorporateKey, empBytes, filename);
        final var count = uploadEmployeesUseCase.execute(command);

        return ResponseEntity.ok(Map.of("importedCount", count, "message", "Successfully imported %d employees".formatted(count)));
    }

    @GetMapping("/{corporateKey}")
    @Operation(summary = "Retrieve an employee by corporate key")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Employee found",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Employee not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EmployeeResponse> getEmployee(
            @Parameter(description = "Corporate Key identifier", example = "CK0001")
            @PathVariable("corporateKey") String corporateKey) {
        return getEmployeeUseCase.execute(new GetEmployeeQuery(corporateKey))
                .map(EmployeeResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ou/{ouId}")
    @Operation(summary = "Query all employees belonging to an Organizational Unit by ID, optionally including subtree")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "List of matching employees",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class)))),
        @ApiResponse(
                responseCode = "404",
                description = "Target organizational unit not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByOuId(
            @Parameter(description = "Target Organizational Unit ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
            @PathVariable("ouId") UUID ouId,
            @Parameter(description = "Whether to recursively include all descendant subtrees", example = "false")
            @RequestParam(name = "includeSubtree", defaultValue = "false") boolean includeSubtree) {

        final var query = GetEmployeesByOuQuery.byId(ouId, includeSubtree);
        final var employees = getEmployeesByOuUseCase.execute(query);
        final var response = employees.stream().map(EmployeeResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Query all employees belonging to an Organizational Unit by name, optionally including subtree")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "List of matching employees",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeResponse.class)))),
        @ApiResponse(
                responseCode = "404",
                description = "Target organizational unit not found",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByOuName(
            @Parameter(description = "Target Organizational Unit name", example = "Engineering Area")
            @RequestParam(name = "ouName") String ouName,
            @Parameter(description = "Whether to recursively include all descendant subtrees", example = "false")
            @RequestParam(name = "includeSubtree", defaultValue = "false") boolean includeSubtree) {

        final var query = GetEmployeesByOuQuery.byName(ouName, includeSubtree);
        final var employees = getEmployeesByOuUseCase.execute(query);
        final var response = employees.stream().map(EmployeeResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
