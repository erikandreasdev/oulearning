package com.example.oulearning.budgeting.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.budgeting.application.port.in.usecase.allocation.AllocateBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.funds.ConsumeBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.allocation.DistributeBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.query.GetBudgetQuery;
import com.example.oulearning.budgeting.application.port.in.usecase.query.GetBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.funds.ReleaseBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.funds.ReserveBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.funds.SpendDirectBudgetFundsUseCase;
import com.example.oulearning.budgeting.application.dto.BudgetDistributionResult;
import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.shared.infrastructure.web.GlobalRestControllerAdvice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;

@WebMvcTest(BudgetRestController.class)
@Import(GlobalRestControllerAdvice.class)
class BudgetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AllocateBudgetUseCase allocateUseCase;

    @MockitoBean
    private GetBudgetUseCase getUseCase;

    @MockitoBean
    private ReserveBudgetFundsUseCase reserveUseCase;

    @MockitoBean
    private ReleaseBudgetFundsUseCase releaseUseCase;

    @MockitoBean
    private ConsumeBudgetFundsUseCase consumeUseCase;

    @MockitoBean
    private SpendDirectBudgetFundsUseCase spendDirectUseCase;

    @MockitoBean
    private DistributeBudgetUseCase distributeUseCase;

    @Nested
    @DisplayName("Allocate Budget Endpoints")
    class AllocateEndpoints {

        @Test
        @DisplayName("should allocate budget and return 201 Created")
        void should_allocateBudget_successfully() throws Exception {
            final var budgetId = UUID.randomUUID();
            final var ouId = UUID.randomUUID();
            final var budget = Budget.of(BudgetId.of(budgetId), OuId.of(ouId), Money.euros(10000.00));

            when(allocateUseCase.execute(any())).thenReturn(budgetId);
            when(getUseCase.execute(any(GetBudgetQuery.class))).thenReturn(Optional.of(budget));

            final var requestJson = """
                    {
                        "budgetId": "%s",
                        "ouId": "%s",
                        "amount": 10000.00,
                        "currencyCode": "EUR"
                    }
                    """.formatted(budgetId, ouId);

            mockMvc.perform(post("/api/v1/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/budgets/" + budgetId))
                    .andExpect(jsonPath("$.budgetId").value(budgetId.toString()))
                    .andExpect(jsonPath("$.fiscalYear").value(2026))
                    .andExpect(jsonPath("$.allocatedAmount").value(10000.00))
                    .andExpect(jsonPath("$.availableAmount").value(10000.00));
        }

        @Test
        @DisplayName("should get budget by OU ID and FiscalYear")
        void should_getBudgetByOuIdAndFiscalYear() throws Exception {
            final var budgetId = UUID.randomUUID();
            final var ouId = UUID.randomUUID();
            final var budget = Budget.of(
                    BudgetId.of(budgetId),
                    OuId.of(ouId),
                    com.example.oulearning.shared.domain.fiscal.FiscalYear.of(2025),
                    Money.euros(15000.00));

            when(getUseCase.execute(new GetBudgetQuery(null, ouId, 2025))).thenReturn(Optional.of(budget));

            mockMvc.perform(get("/api/v1/budgets/ou/{ouId}", ouId)
                            .param("fiscalYear", "2025"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.budgetId").value(budgetId.toString()))
                    .andExpect(jsonPath("$.fiscalYear").value(2025))
                    .andExpect(jsonPath("$.allocatedAmount").value(15000.00));
        }
    }

    @Nested
    @DisplayName("Fund Lifecycle Endpoints")
    class FundLifecycleEndpoints {

        @Test
        @DisplayName("should reserve funds successfully")
        void should_reserveFunds() throws Exception {
            final var budgetId = UUID.randomUUID();
            final var ouId = UUID.randomUUID();
            final var budget = Budget.of(BudgetId.of(budgetId), OuId.of(ouId), Money.euros(10000.00))
                    .reserve(Money.euros(3000.00));

            when(reserveUseCase.execute(any())).thenReturn(budget);

            final var requestJson = """
                    {
                        "amount": 3000.00
                    }
                    """;

            mockMvc.perform(post("/api/v1/budgets/{id}/reserve", budgetId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reservedAmount").value(3000.00))
                    .andExpect(jsonPath("$.availableAmount").value(7000.00));
        }

        @Test
        @DisplayName("should distribute budget among child OUs")
        void should_distributeBudget() throws Exception {
            final var parentOuId = UUID.randomUUID();
            final var childOuId1 = UUID.randomUUID();
            final var childOuId2 = UUID.randomUUID();

            final var parentBudget = Budget.of(BudgetId.of(UUID.randomUUID()), OuId.of(parentOuId), Money.euros(0.00));
            final var child1 = Budget.of(BudgetId.of(UUID.randomUUID()), OuId.of(childOuId1), Money.euros(5000.00));
            final var child2 = Budget.of(BudgetId.of(UUID.randomUUID()), OuId.of(childOuId2), Money.euros(5000.00));

            final var result = new BudgetDistributionResult(parentBudget, List.of(child1, child2));
            when(distributeUseCase.execute(any())).thenReturn(result);

            final var requestJson = """
                    {
                        "parentOuId": "%s",
                        "strategyType": "EQUAL",
                        "childOuIds": ["%s", "%s"]
                    }
                    """.formatted(parentOuId, childOuId1, childOuId2);

            mockMvc.perform(post("/api/v1/budgets/distribute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.childBudgets.length()").value(2))
                    .andExpect(jsonPath("$.parentBudget.availableAmount").value(0.00));
        }
    }
}
