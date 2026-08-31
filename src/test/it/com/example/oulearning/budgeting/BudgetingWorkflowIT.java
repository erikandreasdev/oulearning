package com.example.oulearning.budgeting;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.infrastructure.web.dto.CreateOuBudgetRequest;
import com.example.oulearning.budgeting.infrastructure.web.dto.OuBudgetResponse;
import com.example.oulearning.budgeting.infrastructure.web.dto.PaginatedOuBudgetResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BudgetingWorkflowIT extends AbstractOracleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("given valid budget request with owners, when creating and fetching budgets, then returns expected results with owners")
    @Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-employee.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-ou.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidBudgetRequestWithOwners_whenCreatingAndFetchingBudgets_thenReturnsExpectedResultsWithOwners() {
        // given
        final var existingOuId = 2L;
        final var existingOwnerId = 10L;
        final var randomFiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var randomAssignedBudget = BudgetingTestFactory.randomBigDecimalAmount();

        final var request = new CreateOuBudgetRequest();
        request.setOrganizationalUnitId(existingOuId);
        request.setFiscalYear(randomFiscalYear);
        request.setAssignedBudget(randomAssignedBudget);
        request.setOwners(java.util.List.of(existingOwnerId));
        request.setIncludeAllChildren(false);

        // when
        final var createResponse = restTemplate.postForEntity(
                BudgetingApiEndpoints.BUDGETS_OU, request, PaginatedOuBudgetResponse.class);

        // then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final var createBody = createResponse.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getItems()).hasSize(1);
        assertThat(createBody.getItems().getFirst().getAssignedBudget()).isEqualByComparingTo(randomAssignedBudget);
        assertThat(createBody.getItems().getFirst().getOwners()).containsExactly(existingOwnerId);

        final var budgetId = createBody.getItems().getFirst().getId();

        // when
        final var fetchResponse = restTemplate.getForEntity(
                BudgetingApiEndpoints.BUDGETS_OU_BY_ID.formatted(existingOuId), OuBudgetResponse[].class);

        // then
        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var fetchBody = fetchResponse.getBody();
        assertThat(fetchBody).isNotNull().hasSize(1);
        assertThat(fetchBody[0].getId()).isEqualTo(budgetId);
        assertThat(fetchBody[0].getAssignedBudget()).isEqualByComparingTo(randomAssignedBudget);
        assertThat(fetchBody[0].getAvailableBudget()).isEqualByComparingTo(randomAssignedBudget);
        assertThat(fetchBody[0].getReservedBudget()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(fetchBody[0].getOwners()).containsExactly(existingOwnerId);
    }
}

