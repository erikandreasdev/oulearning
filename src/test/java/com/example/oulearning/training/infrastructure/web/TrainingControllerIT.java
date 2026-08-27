package com.example.oulearning.training.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.application.port.in.command.ListTrainingRequestsQuery;
import com.example.oulearning.training.application.port.in.command.RequestNewTrainingCommand;
import com.example.oulearning.training.application.port.in.command.UpdateTrainingReviewCommand;
import com.example.oulearning.training.application.port.in.model.AreaTrainingItemDto;
import com.example.oulearning.training.application.port.in.model.AreaTrainingsOverviewDto;
import com.example.oulearning.training.application.port.in.model.AttendeeDetailsDto;
import com.example.oulearning.training.application.port.in.model.PaginatedTrainingRequestsResult;
import com.example.oulearning.training.application.port.in.model.TrainingDetailedViewDto;
import com.example.oulearning.training.application.port.in.usecase.GetAreaTrainingsUseCase;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingDetailsUseCase;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.ListTrainingRequestsUseCase;
import com.example.oulearning.training.application.port.in.usecase.RequestNewTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.UpdateTrainingReviewUseCase;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.Modality;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.model.TypeId;
import com.example.oulearning.training.infrastructure.web.dto.RequestNewTrainingRequest;
import com.example.oulearning.training.infrastructure.web.dto.UpdateTrainingReviewRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TrainingController.class)
class TrainingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetAreaTrainingsUseCase getAreaTrainingsUseCase;

    @MockitoBean
    private GetTrainingDetailsUseCase getTrainingDetailsUseCase;

    @MockitoBean
    private RequestNewTrainingUseCase requestNewTrainingUseCase;

    @MockitoBean
    private ListTrainingRequestsUseCase listTrainingRequestsUseCase;

    @MockitoBean
    private UpdateTrainingReviewUseCase updateTrainingReviewUseCase;

    @MockitoBean
    private GetTrainingUseCase getTrainingUseCase;

    @Test
    @DisplayName("given organizational unit id, when getting area trainings, then returns 200")
    void givenOrganizationalUnitId_whenGettingAreaTrainings_thenReturns200() throws Exception {
        // given
        final var ouId = new OrganizationalUnitId(1L);
        final var item = new AreaTrainingItemDto(
                new TrainingId(10L),
                new TrainingName("Java Concurrency"),
                List.of(ouId),
                Cost.of(new BigDecimal("1200.00"), "EUR"),
                TrainingStatus.REQUESTED);
        final var overview = new AreaTrainingsOverviewDto(
                new BigDecimal("50000.00"),
                new BigDecimal("40000.00"),
                List.of(item));
        given(getAreaTrainingsUseCase.execute(eq(ouId))).willReturn(overview);

        // when
        final var result = mockMvc.perform(get("/api/v1/areas/{organizationalUnitId}/trainings", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedBudget").value(50000.0))
                .andExpect(jsonPath("$.availableBudget").value(40000.0))
                .andExpect(jsonPath("$.trainings[0].id").value(10))
                .andExpect(jsonPath("$.trainings[0].name").value("Java Concurrency"));
    }

    @Test
    @DisplayName("given training id, when getting training details, then returns 200")
    void givenTrainingId_whenGettingTrainingDetails_thenReturns200() throws Exception {
        // given
        final var trainingId = new TrainingId(1L);
        final var details = new TrainingDetailedViewDto(
                trainingId,
                new TrainingName("Kubernetes in Action"),
                Cost.of(new BigDecimal("2000.00"), "EUR"),
                "Jane Doe",
                TrainingPurpose.departmentGoals(),
                new TypeId(2L),
                new Hours(24),
                List.of(new AttendeeDetailsDto(new EmployeeId(5L), "John Smith", "john@example.com")));
        given(getTrainingDetailsUseCase.execute(eq(trainingId))).willReturn(details);

        // when
        final var result = mockMvc.perform(get("/api/v1/trainings/{id}/details", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Kubernetes in Action"))
                .andExpect(jsonPath("$.requestedByName").value("Jane Doe"))
                .andExpect(jsonPath("$.attendees[0].email").value("john@example.com"));
    }

    @Test
    @DisplayName("given valid request, when requesting new training, then returns 201")
    void givenValidRequest_whenRequestingNewTraining_thenReturns201() throws Exception {
        // given
        final var request = new RequestNewTrainingRequest();
        request.setRequestedBy(10L);
        request.setOrganizationalUnitId(1L);
        request.setName("Kotlin Coroutines");
        request.setCostAmount(new BigDecimal("1500.00"));
        request.setCostCurrency("EUR");
        request.setHours(16);
        request.setPurposeType(TrainingPurposeType.DEPARTMENT_GOALS.name());
        request.setTypeId(3L);
        request.setAttendees(List.of(10L, 11L));

        final var training = TrainingTestFactory.randomTraining();
        given(requestNewTrainingUseCase.execute(any(RequestNewTrainingCommand.class))).willReturn(training);

        // when
        final var result = mockMvc.perform(post("/api/v1/trainings/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(training.id().value()));
    }

    @Test
    @DisplayName("given filters, when listing training requests, then returns 200")
    void givenFilters_whenListingTrainingRequests_thenReturns200() throws Exception {
        // given
        final var training = TrainingTestFactory.randomTraining();
        final var pagedResult = new PaginatedTrainingRequestsResult(List.of(training), 1L, 1, 0, 20);
        given(listTrainingRequestsUseCase.execute(any(ListTrainingRequestsQuery.class))).willReturn(pagedResult);

        // when
        final var result = mockMvc.perform(get("/api/v1/trainings/requests")
                .param("page", "0")
                .param("size", "20"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.items[0].id").value(training.id().value()));
    }

    @Test
    @DisplayName("given valid review request, when updating training review, then returns 200")
    void givenValidReviewRequest_whenUpdatingTrainingReview_thenReturns200() throws Exception {
        // given
        final var request = new UpdateTrainingReviewRequest();
        request.setComments("Looks good");
        request.setModality(Modality.VIRTUAL.name());
        request.setStartDate(OffsetDateTime.parse("2026-09-01T09:00:00Z"));
        request.setEndDate(OffsetDateTime.parse("2026-09-03T17:00:00Z"));
        request.setReviewedAt(OffsetDateTime.parse("2026-08-27T10:00:00Z"));

        final var training = TrainingTestFactory.randomTraining();
        given(updateTrainingReviewUseCase.execute(any(UpdateTrainingReviewCommand.class))).willReturn(training);

        // when
        final var result = mockMvc.perform(put("/api/v1/trainings/{id}/review", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(training.id().value()));
    }

    @Test
    @DisplayName("given existing training id, when getting training aggregate, then returns 200")
    void givenExistingTrainingId_whenGettingTrainingAggregate_thenReturns200() throws Exception {
        // given
        final var trainingId = new TrainingId(1L);
        final var training = TrainingTestFactory.randomTraining();
        given(getTrainingUseCase.execute(eq(trainingId))).willReturn(training);

        // when
        final var result = mockMvc.perform(get("/api/v1/trainings/{id}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(training.id().value()));
    }
}
