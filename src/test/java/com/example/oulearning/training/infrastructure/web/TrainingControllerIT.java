package com.example.oulearning.training.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.application.port.in.CreateTrainingCommand;
import com.example.oulearning.training.application.port.in.CreateTrainingUseCase;
import com.example.oulearning.training.application.port.in.DeleteTrainingUseCase;
import com.example.oulearning.training.application.port.in.GetTrainingUseCase;
import com.example.oulearning.training.application.port.in.UpdateTrainingCommand;
import com.example.oulearning.training.application.port.in.UpdateTrainingUseCase;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TypeId;
import com.example.oulearning.training.infrastructure.web.dto.CreateTrainingRequest;
import com.example.oulearning.training.infrastructure.web.dto.UpdateTrainingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CreateTrainingUseCase createTrainingUseCase;

    @MockitoBean
    private GetTrainingUseCase getTrainingUseCase;

    @MockitoBean
    private UpdateTrainingUseCase updateTrainingUseCase;

    @MockitoBean
    private DeleteTrainingUseCase deleteTrainingUseCase;

    @Test
    @DisplayName("given valid request, when creating training, then returns 201")
    void givenValidRequest_whenCreatingTraining_thenReturns201() throws Exception {
        // given
        final var request = new CreateTrainingRequest();
        request.setRequestedBy(10L);
        request.setOrganizationalUnitId(1L);
        request.setName("Java Advanced");
        request.setCostAmount(new BigDecimal("1500.00"));
        request.setCurrency("EUR");
        request.setHours(40);
        request.setPurposeType(TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN.name());
        request.setTypeId(5L);

        final var trainingId = new TrainingId(1L);
        final var training = Training.reconstitute(
                trainingId,
                new EmployeeId(10L),
                new OrganizationalUnitId(1L),
                new TrainingName("Java Advanced"),
                Cost.of(new BigDecimal("1500.00"), "EUR"),
                new Hours(40),
                TrainingPurpose.individualDevelopmentPlan(),
                new TypeId(5L),
                TrainingStatus.REQUESTED,
                null,
                Instant.now(),
                Instant.now(),
                Set.of(),
                true);

        given(createTrainingUseCase.execute(any(CreateTrainingCommand.class))).willReturn(trainingId);
        given(getTrainingUseCase.execute(trainingId)).willReturn(training);

        // when
        final var result = mockMvc.perform(post("/api/v1/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Advanced"))
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    @DisplayName("given existing training id, when getting training, then returns 200")
    void givenExistingTrainingId_whenGettingTraining_thenReturns200() throws Exception {
        // given
        final var trainingId = new TrainingId(1L);
        final var training = Training.reconstitute(
                trainingId,
                new EmployeeId(10L),
                new OrganizationalUnitId(1L),
                new TrainingName("Java Advanced"),
                Cost.of(new BigDecimal("1500.00"), "EUR"),
                new Hours(40),
                TrainingPurpose.individualDevelopmentPlan(),
                new TypeId(5L),
                TrainingStatus.REQUESTED,
                null,
                Instant.now(),
                Instant.now(),
                Set.of(),
                true);

        given(getTrainingUseCase.execute(trainingId)).willReturn(training);

        // when
        final var result = mockMvc.perform(get("/api/v1/trainings/{id}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Advanced"));
    }

    @Test
    @DisplayName("given valid request, when updating training, then returns 200")
    void givenValidRequest_whenUpdatingTraining_thenReturns200() throws Exception {
        // given
        final var request = new UpdateTrainingRequest();
        request.setName("Java Expert");
        request.setCostAmount(new BigDecimal("2000.00"));
        request.setCurrency("EUR");
        request.setHours(50);
        request.setPurposeType(TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN.name());
        request.setTypeId(5L);

        final var trainingId = new TrainingId(1L);
        final var training = Training.reconstitute(
                trainingId,
                new EmployeeId(10L),
                new OrganizationalUnitId(1L),
                new TrainingName("Java Expert"),
                Cost.of(new BigDecimal("2000.00"), "EUR"),
                new Hours(50),
                TrainingPurpose.individualDevelopmentPlan(),
                new TypeId(5L),
                TrainingStatus.REQUESTED,
                null,
                Instant.now(),
                Instant.now(),
                Set.of(),
                true);

        given(getTrainingUseCase.execute(trainingId)).willReturn(training);

        // when
        final var result = mockMvc.perform(put("/api/v1/trainings/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Expert"));

        verify(updateTrainingUseCase).execute(any(UpdateTrainingCommand.class));
    }

    @Test
    @DisplayName("given existing training id, when deleting training, then returns 204")
    void givenExistingTrainingId_whenDeletingTraining_thenReturns204() throws Exception {
        // given
        final var trainingId = new TrainingId(1L);

        // when
        final var result = mockMvc.perform(delete("/api/v1/trainings/{id}", 1L));

        // then
        result.andExpect(status().isNoContent());
        verify(deleteTrainingUseCase).execute(trainingId);
    }
}
