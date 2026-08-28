package com.example.oulearning.training;

import static org.assertj.core.api.Assertions.assertThat;


import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.infrastructure.web.dto.AreaTrainingsResponse;
import com.example.oulearning.training.infrastructure.web.dto.PaginatedTrainingRequestsResponse;
import com.example.oulearning.training.infrastructure.web.dto.RequestNewTrainingRequest;
import com.example.oulearning.training.infrastructure.web.dto.TrainingDetailsResponse;
import com.example.oulearning.training.infrastructure.web.dto.TrainingResponse;
import com.example.oulearning.training.infrastructure.web.dto.UpdateTrainingReviewRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrainingWorkflowIT extends AbstractOracleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("given valid training request, when progressing through training lifecycle, then returns expected results")
    @Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-provider.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert-training.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidTrainingRequest_whenProgressingThroughLifecycle_thenReturnsExpectedResults() {
        // given
        final var existingEmployeeId = 11L;
        final var existingOuId = 2L;
        final var existingTypeId = 5L;
        final var existingProviderId = 2L;

        final var randomName = TrainingTestFactory.randomTrainingNameString();
        final var randomCost = TrainingTestFactory.randomBigDecimalCostAmount();
        final var randomHours = TrainingTestFactory.randomHoursValue();

        final var request = new RequestNewTrainingRequest();
        request.setRequestedBy(existingEmployeeId);
        request.setOrganizationalUnitId(existingOuId);
        request.setName(randomName);
        request.setCostAmount(randomCost);
        request.setCostCurrency("EUR");
        request.setHours(randomHours);
        request.setPurposeType("INDIVIDUAL_DEVELOPMENT_PLAN");
        request.setTypeId(existingTypeId);
        request.setAttendees(List.of(existingEmployeeId));

        // when
        final var createResponse = restTemplate.postForEntity(
                TrainingApiEndpoints.TRAINING_REQUESTS, request, TrainingResponse.class);
        
        // then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final var createBody = createResponse.getBody();
        assertThat(createBody).isNotNull();
        assertThat(createBody.getName()).isEqualTo(randomName);
        assertThat(createBody.getStatus()).isEqualTo("REQUESTED");

        final var newTrainingId = createBody.getId();

        // when
        final var getResponse = restTemplate.getForEntity(
                TrainingApiEndpoints.TRAINING_BY_ID.formatted(newTrainingId), TrainingResponse.class);

        // then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var getBody = getResponse.getBody();
        assertThat(getBody).isNotNull();
        assertThat(getBody.getId()).isEqualTo(newTrainingId);

        // when
        final var listResponse = restTemplate.getForEntity(
                TrainingApiEndpoints.TRAINING_REQUESTS_BY_OU.formatted(existingOuId), PaginatedTrainingRequestsResponse.class);

        // then
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var listBody = listResponse.getBody();
        assertThat(listBody).isNotNull();
        assertThat(listBody.getItems()).isNotEmpty();

        // when
        final var updateRequest = new UpdateTrainingReviewRequest();
        updateRequest.setComments("Approved for Q3");
        updateRequest.setModality(TrainingTestFactory.randomModality().name());
        updateRequest.setStartDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1));
        updateRequest.setEndDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(3));
        updateRequest.setExternalProviderId(existingProviderId);
        updateRequest.setReviewedAt(OffsetDateTime.now(ZoneOffset.UTC));

        final var updateResponse = restTemplate.exchange(
                TrainingApiEndpoints.TRAINING_REVIEW.formatted(newTrainingId), HttpMethod.PUT, new HttpEntity<>(updateRequest), TrainingResponse.class);

        // then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var updateBody = updateResponse.getBody();
        assertThat(updateBody).isNotNull();
        assertThat(updateBody.getStatus()).isEqualTo("REQUESTED");

        // when
        final var detailsResponse = restTemplate.getForEntity(
                TrainingApiEndpoints.TRAINING_DETAILS.formatted(newTrainingId), TrainingDetailsResponse.class);

        // then
        assertThat(detailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var detailsBody = detailsResponse.getBody();
        assertThat(detailsBody).isNotNull();
        assertThat(detailsBody.getId()).isEqualTo(newTrainingId);

        // when
        final var areaTrainingsResponse = restTemplate.getForEntity(
                TrainingApiEndpoints.AREA_TRAININGS.formatted(existingOuId), AreaTrainingsResponse.class);

        // then
        assertThat(areaTrainingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var areaTrainingsBody = areaTrainingsResponse.getBody();
        assertThat(areaTrainingsBody).isNotNull();
        assertThat(areaTrainingsBody.getTrainings()).isNotEmpty();
    }
}
