package com.example.oulearning.training.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class TrainingRestFlowIT {

    @Container
    static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Complete Training Request Lifecycle: create OU -> allocate Budget -> register Manager & Employees -> submit DRAFT (reserves budget) -> manager approves (consumes budget) -> manager rejects (releases budget) -> non-manager fails -> search by OU/Status")
    void should_executeCompleteTrainingRequestFlow() throws Exception {
        final var engineeringOuId = UUID.randomUUID();
        final var marketingOuId = UUID.randomUUID();

        // 1. Create Engineering OU with owner CK0001
        final var createEngOuJson = """
                {
                    "id": "%s",
                    "name": "Engineering Department",
                    "description": "Software & Platform Engineering",
                    "ownerCorporateKeys": ["CK0001"]
                }
                """.formatted(engineeringOuId);

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createEngOuJson))
                .andExpect(status().isCreated());

        // 2. Create Marketing OU with owner CK0090
        final var createMktOuJson = """
                {
                    "id": "%s",
                    "name": "Marketing Department",
                    "description": "Product Marketing",
                    "ownerCorporateKeys": ["CK0090"]
                }
                """.formatted(marketingOuId);

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createMktOuJson))
                .andExpect(status().isCreated());

        // 3. Allocate 10,000 EUR Budget for Engineering OU in current Fiscal Year 2026
        final var allocateBudgetJson = """
                {
                    "ouId": "%s",
                    "fiscalYear": 2026,
                    "amount": 10000.00,
                    "currency": "EUR"
                }
                """.formatted(engineeringOuId);

        mockMvc.perform(post("/api/v1/budgets/allocate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(allocateBudgetJson))
                .andExpect(status().isCreated());

        // 4. Register Manager (CK0001) in Engineering OU
        final var emp1Json = """
                {
                    "corporateKey": "CK0001",
                    "firstName": "Alice",
                    "lastName": "Director",
                    "email": "alice.director@company.com",
                    "phone": "+34600111222",
                    "role": "MANAGER",
                    "ouId": "%s"
                }
                """.formatted(engineeringOuId);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emp1Json))
                .andExpect(status().isCreated());

        // 5. Register Employee (CK0002) in Engineering OU
        final var emp2Json = """
                {
                    "corporateKey": "CK0002",
                    "firstName": "Bob",
                    "lastName": "SeniorDev",
                    "email": "bob.seniordev@company.com",
                    "phone": "+34600333444",
                    "role": "EMPLOYEE",
                    "ouId": "%s"
                }
                """.formatted(engineeringOuId);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emp2Json))
                .andExpect(status().isCreated());

        // 6. Register Employee (CK0099) in Marketing OU
        final var empOtherJson = """
                {
                    "corporateKey": "CK0099",
                    "firstName": "Charlie",
                    "lastName": "Marketer",
                    "email": "charlie.mkt@company.com",
                    "role": "EMPLOYEE",
                    "ouId": "%s"
                }
                """.formatted(marketingOuId);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(empOtherJson))
                .andExpect(status().isCreated());

        // 7. Submit Training Request 1 (1,500 EUR, UPSKILLING) -> Initial status is DRAFT
        final var tr1Id = UUID.randomUUID();
        final var tr1Json = """
                {
                    "id": "%s",
                    "ouId": "%s",
                    "requesterCorporateKey": "CK0001",
                    "name": "Advanced Domain-Driven Design and Hexagonal Architecture",
                    "costAmount": 1500.00,
                    "costCurrency": "EUR",
                    "purposeType": "UPSKILLING",
                    "trainingHours": 40,
                    "availableAtOrgUniversity": true,
                    "assistantCorporateKeys": ["CK0001", "CK0002"]
                }
                """.formatted(tr1Id, engineeringOuId);

        mockMvc.perform(post("/api/v1/training-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tr1Json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/training-requests/" + tr1Id))
                .andExpect(jsonPath("$.id").value(tr1Id.toString()))
                .andExpect(jsonPath("$.ouId").value(engineeringOuId.toString()))
                .andExpect(jsonPath("$.requesterCorporateKey").value("CK0001"))
                .andExpect(jsonPath("$.name").value("Advanced Domain-Driven Design and Hexagonal Architecture"))
                .andExpect(jsonPath("$.costAmount").value(1500.00))
                .andExpect(jsonPath("$.purposeType").value("UPSKILLING"))
                .andExpect(jsonPath("$.trainingHours").value(40))
                .andExpect(jsonPath("$.availableAtOrgUniversity").value(true))
                .andExpect(jsonPath("$.fiscalYear").value(2026))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.assistants.length()").value(2));

        // Verify Budget has 1,500 EUR reserved
        mockMvc.perform(get("/api/v1/budgets")
                        .param("ouId", engineeringOuId.toString())
                        .param("fiscalYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allocatedAmount").value(10000.00))
                .andExpect(jsonPath("$.reservedAmount").value(1500.00))
                .andExpect(jsonPath("$.spentAmount").value(0.00));

        // 8. Submit Training Request 2 (3,200 EUR, OTHER purpose) -> Initial status is DRAFT
        final var tr2Id = UUID.randomUUID();
        final var tr2Json = """
                {
                    "id": "%s",
                    "ouId": "%s",
                    "requesterCorporateKey": "CK0001",
                    "name": "Generative AI Agents Workflow Workshop",
                    "costAmount": 3200.00,
                    "costCurrency": "EUR",
                    "purposeType": "OTHER",
                    "purposeCustomText": "Specialized team workflow automation using deep learning agents",
                    "trainingHours": 32,
                    "availableAtOrgUniversity": false,
                    "assistantCorporateKeys": ["CK0002"]
                }
                """.formatted(tr2Id, engineeringOuId);

        mockMvc.perform(post("/api/v1/training-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tr2Json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(tr2Id.toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        // Verify Budget has 4,700 EUR reserved (1500 + 3200)
        mockMvc.perform(get("/api/v1/budgets")
                        .param("ouId", engineeringOuId.toString())
                        .param("fiscalYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedAmount").value(4700.00))
                .andExpect(jsonPath("$.spentAmount").value(0.00));

        // 9. Non-manager CK0002 attempts to approve Request 1 -> 422 Problem Details (UnauthorizedManagerException)
        final var unauthApproveJson = """
                {
                    "managerCorporateKey": "CK0002",
                    "managerNotes": "Trying to self-approve"
                }
                """;

        mockMvc.perform(post("/api/v1/training-requests/{id}/approve", tr1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unauthApproveJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Domain Rule Violation"))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Required role: MANAGER")));

        // 10. Manager CK0001 approves Request 1 -> status becomes APPROVED, Budget consumes 1,500 EUR
        final var approveJson = """
                {
                    "managerCorporateKey": "CK0001",
                    "managerNotes": "Approved for Q3 Engineering initiative"
                }
                """;

        mockMvc.perform(post("/api/v1/training-requests/{id}/approve", tr1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(approveJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tr1Id.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reviewedBy").value("CK0001"))
                .andExpect(jsonPath("$.managerNotes").value("Approved for Q3 Engineering initiative"))
                .andExpect(jsonPath("$.reviewedAt").isNotEmpty());

        // Verify Budget: reserved reduced to 3,200 EUR, spent increased to 1,500 EUR
        mockMvc.perform(get("/api/v1/budgets")
                        .param("ouId", engineeringOuId.toString())
                        .param("fiscalYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedAmount").value(3200.00))
                .andExpect(jsonPath("$.spentAmount").value(1500.00));

        // 11. Manager CK0001 rejects Request 2 with reason -> status becomes REJECTED, Budget releases 3,200 EUR
        final var rejectJson = """
                {
                    "managerCorporateKey": "CK0001",
                    "rejectionReason": "Cost exceeds department budget allocation for this quarter",
                    "managerNotes": "Consider internal OrgUniversity course instead"
                }
                """;

        mockMvc.perform(post("/api/v1/training-requests/{id}/reject", tr2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rejectJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tr2Id.toString()))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.reviewedBy").value("CK0001"))
                .andExpect(jsonPath("$.rejectionReason").value("Cost exceeds department budget allocation for this quarter"))
                .andExpect(jsonPath("$.managerNotes").value("Consider internal OrgUniversity course instead"))
                .andExpect(jsonPath("$.reviewedAt").isNotEmpty());

        // Verify Budget: reserved reduced to 0.00 EUR, spent remains 1,500 EUR
        mockMvc.perform(get("/api/v1/budgets")
                        .param("ouId", engineeringOuId.toString())
                        .param("fiscalYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedAmount").value(0.00))
                .andExpect(jsonPath("$.spentAmount").value(1500.00));

        // 12. Search & filter queries for Manager view
        // Search by OU Name "Engineering Department" and status "APPROVED" -> 1 result (tr1)
        mockMvc.perform(get("/api/v1/training-requests")
                        .param("ouNames", "Engineering Department")
                        .param("status", "APPROVED")
                        .param("fiscalYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(tr1Id.toString()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        // Search by OU Name "Engineering Department" and status "REJECTED" -> 1 result (tr2)
        mockMvc.perform(get("/api/v1/training-requests")
                        .param("ouNames", "Engineering Department")
                        .param("status", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(tr2Id.toString()))
                .andExpect(jsonPath("$[0].status").value("REJECTED"));

        // Search by status "DRAFT" -> 0 results
        mockMvc.perform(get("/api/v1/training-requests")
                        .param("status", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
