package com.example.oulearning.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.budgeting.infrastructure.web.dto.CreateOuBudgetRequest;
import com.example.oulearning.budgeting.infrastructure.web.dto.PaginatedOuBudgetResponse;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.infrastructure.web.dto.OuBudgetResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BudgetingWorkflowIT {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer(DockerImageName.parse("gvenzl/oracle-free:23-slim").asCompatibleSubstituteFor("gvenzl/oracle-xe"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("given valid budget request, when creating and fetching budgets, then returns expected results")
    @Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-ou.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidBudgetRequest_whenCreatingAndFetchingBudgets_thenReturnsExpectedResults() {
        // given
        final var existingOuId = 2L; // Assigned by insert-ou.sql
        final var randomFiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var randomAssignedBudget = BudgetingTestFactory.randomBigDecimalAmount();

        final var request = new CreateOuBudgetRequest();
        request.setOrganizationalUnitId(existingOuId);
        request.setFiscalYear(randomFiscalYear);
        request.setAssignedBudget(randomAssignedBudget);
        request.setIncludeAllChildren(false);

        // when
        final var createResponse = restTemplate.postForEntity(
                "/api/v1/budgets/organizational-unit", request, PaginatedOuBudgetResponse.class);

        // then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getItems()).hasSize(1);
        assertThat(createResponse.getBody().getItems().get(0).getAssignedBudget()).isEqualByComparingTo(randomAssignedBudget);

        final var budgetId = createResponse.getBody().getItems().get(0).getId();

        // when (fetch)
        final var fetchResponse = restTemplate.getForEntity(
                "/api/v1/budgets/organizational-unit/" + existingOuId, OuBudgetResponse[].class);

        // then (fetch)
        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchResponse.getBody()).isNotNull().hasSize(1);
        assertThat(fetchResponse.getBody()[0].getId()).isEqualTo(budgetId);
        assertThat(fetchResponse.getBody()[0].getAssignedBudget()).isEqualByComparingTo(randomAssignedBudget);
        assertThat(fetchResponse.getBody()[0].getAvailableBudget()).isEqualByComparingTo(randomAssignedBudget);
        assertThat(fetchResponse.getBody()[0].getReservedBudget()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
