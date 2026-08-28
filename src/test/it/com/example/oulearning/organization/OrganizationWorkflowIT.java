package com.example.oulearning.organization;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.infrastructure.web.dto.AssignEmployeeRequest;
import com.example.oulearning.organization.infrastructure.web.dto.CreateEmployeeRequest;
import com.example.oulearning.organization.infrastructure.web.dto.CreateOrganizationalUnitRequest;
import com.example.oulearning.organization.infrastructure.web.dto.EmployeeResponse;
import com.example.oulearning.organization.infrastructure.web.dto.OrganizationalUnitResponse;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationWorkflowIT extends AbstractOracleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("given valid employee request, when progressing through lifecycle, then returns expected results")
    @Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidEmployeeRequest_whenProgressingThroughLifecycle_thenReturnsExpectedResults() {
        // given
        final var randomName = EmployeeTestFactory.randomNameString();
        final var randomSurname = EmployeeTestFactory.randomSurnameString();
        final var randomEmail = EmployeeTestFactory.randomEmailString();

        final var createRequest = new CreateEmployeeRequest();
        createRequest.setName(randomName);
        createRequest.setSurname(randomSurname);
        createRequest.setEmail(randomEmail);

        // when
        final var createResponse = restTemplate.postForEntity(
                OrganizationApiEndpoints.EMPLOYEES, createRequest, EmployeeResponse.class);

        // then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final var createBody = Objects.requireNonNull(createResponse.getBody());
        assertThat(createBody.getName()).isEqualTo(randomName);
        assertThat(createBody.getSurname()).isEqualTo(randomSurname);
        assertThat(createBody.getEmail()).isEqualTo(randomEmail);

        final var employeeId = createBody.getId();

        // when
        final var getResponse = restTemplate.getForEntity(
                OrganizationApiEndpoints.EMPLOYEE_BY_ID.formatted(employeeId), EmployeeResponse.class);

        // then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var getBody = Objects.requireNonNull(getResponse.getBody());
        assertThat(getBody.getId()).isEqualTo(employeeId);

        // when
        restTemplate.delete(OrganizationApiEndpoints.EMPLOYEE_BY_ID.formatted(employeeId));
        final var getDeletedResponse = restTemplate.getForEntity(
                OrganizationApiEndpoints.EMPLOYEE_BY_ID.formatted(employeeId), EmployeeResponse.class);

        // then
        assertThat(getDeletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("given valid organizational unit request, when progressing through lifecycle, then returns expected results")
    @Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-employee.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidOuRequest_whenProgressingThroughLifecycle_thenReturnsExpectedResults() {
        // given
        final var existingEmployeeId = 2L;
        final var randomName = HierarchyTestFactory.randomOrganizationalUnitNameString();

        final var createRequest = new CreateOrganizationalUnitRequest();
        createRequest.setName(randomName);

        // when
        final var createResponse = restTemplate.postForEntity(
                OrganizationApiEndpoints.ORGANIZATIONAL_UNITS, createRequest, OrganizationalUnitResponse.class);

        // then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final var createBody = Objects.requireNonNull(createResponse.getBody());
        assertThat(createBody.getName()).isEqualTo(randomName);

        final var ouId = createBody.getId();

        // when
        final var assignRequest = new AssignEmployeeRequest();
        assignRequest.setEmployeeId(existingEmployeeId);

        final var assignMemberResponse = restTemplate.postForEntity(
                OrganizationApiEndpoints.ORGANIZATIONAL_UNIT_MEMBERS.formatted(ouId), assignRequest, Void.class);

        // then
        assertThat(assignMemberResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // when
        final var getResponse = restTemplate.getForEntity(
                OrganizationApiEndpoints.ORGANIZATIONAL_UNIT_BY_ID.formatted(ouId), OrganizationalUnitResponse.class);

        // then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var getBody = Objects.requireNonNull(getResponse.getBody());
        assertThat(getBody.getMembers()).contains(existingEmployeeId);
    }
}
