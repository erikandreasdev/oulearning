package com.example.oulearning;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.time.Clock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class OulearningApplicationTests {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer(
            DockerImageName.parse("gvenzl/oracle-free:23-slim").asCompatibleSubstituteFor("gvenzl/oracle-xe"));

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private com.example.oulearning.organization.domain.employee.model.IdGenerator employeeIdGenerator;

    @MockitoBean
    private OrganizationalUnitRepository organizationalUnitRepository;

    @MockitoBean
    private com.example.oulearning.organization.domain.hierarchy.model.IdGenerator ouIdGenerator;

    @MockitoBean
    private BudgetRepository budgetRepository;

    @MockitoBean
    private com.example.oulearning.budgeting.domain.model.IdGenerator budgetIdGenerator;

    @MockitoBean
    private TrainingRepository trainingRepository;

    @MockitoBean
    private com.example.oulearning.training.domain.model.IdGenerator trainingIdGenerator;

    @MockitoBean
    private Clock clock;

    @Test
    @DisplayName("given application context, when starting up, then context loads successfully")
    void givenAppContext_whenStartingUp_thenContextLoads(final ApplicationContext context) {
        // given

        // when

        // then
        assertThat(context).isNotNull();
    }
}
