package com.example.oulearning.budgeting.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class BudgetRestFlowIT {

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
    @DisplayName("Complete Budget flow: allocate -> distribute -> reserve -> consume -> spend-direct -> verify domain validation error")
    void should_executeCompleteBudgetLifecycleFlow() throws Exception {
        final var parentOuId = UUID.randomUUID();
        final var childOu1Id = UUID.randomUUID();
        final var childOu2Id = UUID.randomUUID();
        final var parentBudgetId = UUID.randomUUID();

        // 1. Allocate initial budget to Parent OU (20,000 EUR)
        final var allocateJson = """
                {
                    "budgetId": "%s",
                    "ouId": "%s",
                    "amount": 20000.00,
                    "currencyCode": "EUR"
                }
                """.formatted(parentBudgetId, parentOuId);

        mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(allocateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.budgetId").value(parentBudgetId.toString()))
                .andExpect(jsonPath("$.allocatedAmount").value(20000.00))
                .andExpect(jsonPath("$.availableAmount").value(20000.00));

        // 2. Distribute Budget equally among Child OUs (10,000 EUR each)
        final var distributeJson = """
                {
                    "parentOuId": "%s",
                    "strategyType": "EQUAL",
                    "childOuIds": ["%s", "%s"]
                }
                """.formatted(parentOuId, childOu1Id, childOu2Id);

        mockMvc.perform(post("/api/v1/budgets/distribute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(distributeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parentBudget.availableAmount").value(0.00))
                .andExpect(jsonPath("$.childBudgets.length()").value(2))
                .andExpect(jsonPath("$.childBudgets[0].allocatedAmount").value(10000.00));

        // 3. Query Child 1 Budget by OU ID
        final var child1Result = mockMvc.perform(get("/api/v1/budgets/ou/{ouId}", childOu1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allocatedAmount").value(10000.00))
                .andExpect(jsonPath("$.availableAmount").value(10000.00))
                .andReturn();

        final var child1ResponseStr = child1Result.getResponse().getContentAsString();
        final var child1BudgetId = com.jayway.jsonpath.JsonPath.read(child1ResponseStr, "$.budgetId");

        // 4. Reserve 3,000 EUR on Child 1 Budget
        final var reserveJson = """
                {
                    "amount": 3000.00
                }
                """;

        mockMvc.perform(post("/api/v1/budgets/{id}/reserve", child1BudgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reserveJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedAmount").value(3000.00))
                .andExpect(jsonPath("$.availableAmount").value(7000.00));

        // 5. Consume 2,000 EUR on Child 1 Budget
        final var consumeJson = """
                {
                    "amount": 2000.00
                }
                """;

        mockMvc.perform(post("/api/v1/budgets/{id}/consume", child1BudgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(consumeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedAmount").value(1000.00))
                .andExpect(jsonPath("$.spentAmount").value(2000.00))
                .andExpect(jsonPath("$.availableAmount").value(7000.00));

        // 6. Direct Spend 1,000 EUR on Child 1 Budget
        final var spendDirectJson = """
                {
                    "amount": 1000.00
                }
                """;

        mockMvc.perform(post("/api/v1/budgets/{id}/spend-direct", child1BudgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(spendDirectJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservedAmount").value(1000.00))
                .andExpect(jsonPath("$.spentAmount").value(3000.00))
                .andExpect(jsonPath("$.availableAmount").value(6000.00));

        // 7. Negative Test: Attempt to reserve more than available (8,000 > 6,000) -> 422 Problem Details
        final var excessiveReserveJson = """
                {
                    "amount": 8000.00
                }
                """;

        mockMvc.perform(post("/api/v1/budgets/{id}/reserve", child1BudgetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(excessiveReserveJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Domain Rule Violation"))
                .andExpect(jsonPath("$.type").value("urn:problem-type:domain-rule-violation"));
    }
}
