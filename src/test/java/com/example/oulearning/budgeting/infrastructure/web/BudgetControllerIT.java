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
        final var ouId = new OrganizationalUnitId(1L);
        final var budgetDto = new OrganizationalUnitBudgetDto(
                new BudgetId(10L),
                ouId,
                Money.of(new BigDecimal("10000.00")),
                Money.of(new BigDecimal("8000.00")),
                Money.of(new BigDecimal("2000.00")),
                new FiscalYear(2025),
                List.of());
        given(getBudgetsByOrganizationalUnitUseCase.execute(eq(ouId), eq(false)))
                .willReturn(List.of(budgetDto));

        // when
        final var result = mockMvc.perform(get("/api/v1/budgets/organizational-unit/{organizationalUnitId}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].organizationalUnitId").value(1))
                .andExpect(jsonPath("$[0].fiscalYear").value(2025))
                .andExpect(jsonPath("$[0].assignedBudget").value(10000.0))
                .andExpect(jsonPath("$[0].availableBudget").value(8000.0))
                .andExpect(jsonPath("$[0].reservedBudget").value(2000.0));
    }

    @Test
    @DisplayName("given valid create budget request, when creating budgets by OU, then returns 201")
    void givenValidCreateBudgetRequest_whenCreatingBudgetsByOu_thenReturns201() throws Exception {
        // given
        final var request = new CreateOuBudgetRequest();
        request.setOrganizationalUnitId(1L);
        request.setFiscalYear(2025);
        request.setAssignedBudget(new BigDecimal("15000.00"));
        request.setIncludeAllChildren(false);

        final var budgetDto = new OrganizationalUnitBudgetDto(
                new BudgetId(20L),
                new OrganizationalUnitId(1L),
                Money.of(new BigDecimal("15000.00")),
                Money.of(new BigDecimal("15000.00")),
                Money.of(BigDecimal.ZERO),
                new FiscalYear(2025),
                List.of());
        final var pagedResult = new PaginatedBudgetsResult(List.of(budgetDto), 1, 1, 0, 20);
        given(createOrganizationalUnitBudgetsUseCase.execute(any(CreateOrganizationalUnitBudgetsCommand.class)))
                .willReturn(pagedResult);

        // when
        final var result = mockMvc.perform(post("/api/v1/budgets/organizational-unit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].id").value(20))
                .andExpect(jsonPath("$.items[0].assignedBudget").value(15000.0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
