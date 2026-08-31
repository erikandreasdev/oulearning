package com.example.oulearning.budgeting.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.budgeting.application.port.in.command.CreateOrganizationalUnitBudgetsCommand;
import com.example.oulearning.budgeting.application.port.in.model.OrganizationalUnitBudgetDto;
import com.example.oulearning.budgeting.application.port.in.model.PaginatedBudgetsResult;
import com.example.oulearning.budgeting.application.port.in.usecase.CreateOrganizationalUnitBudgetsUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.GetBudgetsByOrganizationalUnitUseCase;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.budgeting.infrastructure.web.dto.CreateOuBudgetRequest;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.BudgetingApiEndpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BudgetController.class)
class BudgetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetBudgetsByOrganizationalUnitUseCase getBudgetsByOrganizationalUnitUseCase;

    @MockitoBean
    private CreateOrganizationalUnitBudgetsUseCase createOrganizationalUnitBudgetsUseCase;

    @Test
    @DisplayName("given organizational unit id, when getting budgets by OU, then returns 200")
    void givenOrganizationalUnitId_whenGettingBudgetsByOu_thenReturns200() throws Exception {
        // given
        final var ouId = new OrganizationalUnitId(BudgetingTestFactory.randomId());
        final var budgetId = new BudgetId(BudgetingTestFactory.randomId());
        final var assignedBudget = BudgetingTestFactory.randomBigDecimalAmount();
        final var availableBudget = BudgetingTestFactory.randomBigDecimalAmount();
        final var reservedBudget = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        
        final var budgetDto = new OrganizationalUnitBudgetDto(
                budgetId,
                ouId,
                Money.of(assignedBudget),
                Money.of(availableBudget),
                Money.of(reservedBudget),
                new FiscalYear(fiscalYear),
                List.of());
        given(getBudgetsByOrganizationalUnitUseCase.execute(eq(ouId), eq(false)))
                .willReturn(List.of(budgetDto));

        // when
        final var result = mockMvc.perform(get(BudgetingApiEndpoints.BUDGETS_OU_BY_ID.formatted(ouId.value())));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(budgetId.value()))
                .andExpect(jsonPath("$[0].organizationalUnitId").value(ouId.value()))
                .andExpect(jsonPath("$[0].fiscalYear").value(fiscalYear))
                .andExpect(jsonPath("$[0].assignedBudget").value(assignedBudget.doubleValue()))
                .andExpect(jsonPath("$[0].availableBudget").value(availableBudget.doubleValue()))
                .andExpect(jsonPath("$[0].reservedBudget").value(reservedBudget.doubleValue()));
    }

    @Test
    @DisplayName("given valid create budget request, when creating budgets by OU, then returns 201")
    void givenValidCreateBudgetRequest_whenCreatingBudgetsByOu_thenReturns201() throws Exception {
        // given
        final var ouId = BudgetingTestFactory.randomId();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var assignedBudget = BudgetingTestFactory.randomBigDecimalAmount();
        
        final var request = new CreateOuBudgetRequest();
        request.setOrganizationalUnitId(ouId);
        request.setFiscalYear(fiscalYear);
        request.setAssignedBudget(assignedBudget);
        request.setOwners(List.of(BudgetingTestFactory.randomId()));
        request.setIncludeAllChildren(false);

        final var budgetId = BudgetingTestFactory.randomId();
        final var budgetDto = new OrganizationalUnitBudgetDto(
                new BudgetId(budgetId),
                new OrganizationalUnitId(ouId),
                Money.of(assignedBudget),
                Money.of(assignedBudget),
                Money.of(BigDecimal.ZERO),
                new FiscalYear(fiscalYear),
                List.of());
        final var pagedResult = new PaginatedBudgetsResult(List.of(budgetDto), 1, 1, 0, 20);
        given(createOrganizationalUnitBudgetsUseCase.execute(any(CreateOrganizationalUnitBudgetsCommand.class)))
                .willReturn(pagedResult);

        // when
        final var result = mockMvc.perform(post(BudgetingApiEndpoints.BUDGETS_OU)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].id").value(budgetId))
                .andExpect(jsonPath("$.items[0].assignedBudget").value(assignedBudget.doubleValue()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
