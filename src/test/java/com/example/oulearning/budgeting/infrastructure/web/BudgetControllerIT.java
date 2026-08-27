package com.example.oulearning.budgeting.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.budgeting.application.port.in.command.CreateBudgetCommand;
import com.example.oulearning.budgeting.application.port.in.command.UpdateBudgetCommand;
import com.example.oulearning.budgeting.application.port.in.usecase.CreateBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.DeleteBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.GetBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.UpdateBudgetUseCase;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.budgeting.infrastructure.web.dto.CreateBudgetRequest;
import com.example.oulearning.budgeting.infrastructure.web.dto.UpdateBudgetRequest;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CreateBudgetUseCase createBudgetUseCase;

    @MockitoBean
    private GetBudgetUseCase getBudgetUseCase;

    @MockitoBean
    private UpdateBudgetUseCase updateBudgetUseCase;

    @MockitoBean
    private DeleteBudgetUseCase deleteBudgetUseCase;

    @Test
    @DisplayName("given valid request, when creating budget, then returns 201")
    void givenValidRequest_whenCreatingBudget_thenReturns201() throws Exception {
        // given
        final var request = new CreateBudgetRequest();
        request.setOrganizationalUnitId(1L);
        request.setFiscalYear(2025);
        request.setTotalAmount(new BigDecimal("10000.00"));
        request.setReservedAmount(new BigDecimal("0.00"));
        request.setAvailableAmount(new BigDecimal("10000.00"));

        final var budgetId = new BudgetId(1L);
        final var budget = Budget.reconstitute(
                budgetId,
                new OrganizationalUnitId(1L),
                new FiscalYear(2025),
                Money.of(new BigDecimal("10000.00")),
                Money.of(new BigDecimal("0.00")),
                Money.of(new BigDecimal("10000.00")),
                true);

        given(createBudgetUseCase.execute(any(CreateBudgetCommand.class))).willReturn(budgetId);
        given(getBudgetUseCase.execute(budgetId)).willReturn(budget);

        // when
        final var result = mockMvc.perform(post("/api/v1/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.organizationalUnitId").value(1))
                .andExpect(jsonPath("$.fiscalYear").value(2025))
                .andExpect(jsonPath("$.totalAmount").value(10000.0))
                .andExpect(jsonPath("$.reservedAmount").value(0.0))
                .andExpect(jsonPath("$.availableAmount").value(10000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("given existing budget id, when getting budget, then returns 200")
    void givenExistingBudgetId_whenGettingBudget_thenReturns200() throws Exception {
        // given
        final var budgetId = new BudgetId(1L);
        final var budget = Budget.reconstitute(
                budgetId,
                new OrganizationalUnitId(1L),
                new FiscalYear(2025),
                Money.of(new BigDecimal("10000.00")),
                Money.of(new BigDecimal("1000.00")),
                Money.of(new BigDecimal("9000.00")),
                true);

        given(getBudgetUseCase.execute(budgetId)).willReturn(budget);

        // when
        final var result = mockMvc.perform(get("/api/v1/budgets/{id}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.organizationalUnitId").value(1))
                .andExpect(jsonPath("$.fiscalYear").value(2025))
                .andExpect(jsonPath("$.totalAmount").value(10000.0))
                .andExpect(jsonPath("$.reservedAmount").value(1000.0))
                .andExpect(jsonPath("$.availableAmount").value(9000.0));
    }

    @Test
    @DisplayName("given valid request, when updating budget, then returns 200")
    void givenValidRequest_whenUpdatingBudget_thenReturns200() throws Exception {
        // given
        final var request = new UpdateBudgetRequest();
        request.setTotalAmount(new BigDecimal("20000.00"));
        request.setReservedAmount(new BigDecimal("0.00"));
        request.setAvailableAmount(new BigDecimal("20000.00"));

        final var budgetId = new BudgetId(1L);
        final var budget = Budget.reconstitute(
                budgetId,
                new OrganizationalUnitId(1L),
                new FiscalYear(2025),
                Money.of(new BigDecimal("20000.00")),
                Money.of(new BigDecimal("0.00")),
                Money.of(new BigDecimal("20000.00")),
                true);

        given(getBudgetUseCase.execute(budgetId)).willReturn(budget);

        // when
        final var result = mockMvc.perform(put("/api/v1/budgets/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(20000.0));

        verify(updateBudgetUseCase).execute(any(UpdateBudgetCommand.class));
    }

    @Test
    @DisplayName("given existing budget id, when deleting budget, then returns 204")
    void givenExistingBudgetId_whenDeletingBudget_thenReturns204() throws Exception {
        // given
        final var budgetId = new BudgetId(1L);

        // when
        final var result = mockMvc.perform(delete("/api/v1/budgets/{id}", 1L));

        // then
        result.andExpect(status().isNoContent());
        verify(deleteBudgetUseCase).execute(budgetId);
    }
}
