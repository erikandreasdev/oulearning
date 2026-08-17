package com.example.oulearning.training.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.shared.infrastructure.web.GlobalRestControllerAdvice;
import com.example.oulearning.training.application.ApproveTrainingRequestUseCase;
import com.example.oulearning.training.application.GetTrainingRequestQuery;
import com.example.oulearning.training.application.GetTrainingRequestUseCase;
import com.example.oulearning.training.application.GetTrainingRequestsUseCase;
import com.example.oulearning.training.application.RejectTrainingRequestUseCase;
import com.example.oulearning.training.application.SubmitTrainingRequestUseCase;
import com.example.oulearning.training.domain.CorporateKey;
import com.example.oulearning.training.domain.ManagerNotes;
import com.example.oulearning.training.domain.OuId;
import com.example.oulearning.training.domain.RejectionReason;
import com.example.oulearning.training.domain.TrainingCost;
import com.example.oulearning.training.domain.TrainingHours;
import com.example.oulearning.training.domain.TrainingName;
import com.example.oulearning.training.domain.TrainingPurpose;
import com.example.oulearning.training.domain.TrainingPurposeType;
import com.example.oulearning.training.domain.TrainingRequest;
import com.example.oulearning.training.domain.TrainingRequestId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TrainingRequestRestController.class)
@Import(GlobalRestControllerAdvice.class)
class TrainingRequestRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubmitTrainingRequestUseCase submitUseCase;

    @MockitoBean
    private ApproveTrainingRequestUseCase approveUseCase;

    @MockitoBean
    private RejectTrainingRequestUseCase rejectUseCase;

    @MockitoBean
    private GetTrainingRequestUseCase getUseCase;

    @MockitoBean
    private GetTrainingRequestsUseCase searchUseCase;

    @Test
    @DisplayName("should submit training request and return 201 Created")
    void should_submitTrainingRequest() throws Exception {
        final var requestId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        final var request = TrainingRequest.create(
                TrainingRequestId.of(requestId),
                OuId.of(ouId),
                CorporateKey.of("CK0001"),
                TrainingName.of("Hexagonal Architecture"),
                TrainingCost.euros(1500.00),
                TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                TrainingHours.of(40),
                true,
                Set.of(CorporateKey.of("CK0002")),
                FiscalYear.of(2026),
                Instant.now());

        when(submitUseCase.execute(any())).thenReturn(requestId);
        when(getUseCase.execute(new GetTrainingRequestQuery(requestId))).thenReturn(Optional.of(request));

        final var json = """
                {
                    "ouId": "%s",
                    "requesterCorporateKey": "CK0001",
                    "name": "Hexagonal Architecture",
                    "costAmount": 1500.00,
                    "costCurrency": "EUR",
                    "purposeType": "UPSKILLING",
                    "trainingHours": 40,
                    "availableAtOrgUniversity": true,
                    "assistantCorporateKeys": ["CK0002"]
                }
                """.formatted(ouId);

        mockMvc.perform(post("/api/v1/training-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/training-requests/" + requestId))
                .andExpect(jsonPath("$.id").value(requestId.toString()))
                .andExpect(jsonPath("$.name").value("Hexagonal Architecture"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.fiscalYear").value(2026))
                .andExpect(jsonPath("$.assistants[0]").value("CK0002"));
    }

    @Test
    @DisplayName("should approve training request and return 200 OK")
    void should_approveTrainingRequest() throws Exception {
        final var requestId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        final var initial = TrainingRequest.create(
                TrainingRequestId.of(requestId),
                OuId.of(ouId),
                CorporateKey.of("CK0001"),
                TrainingName.of("Hexagonal Architecture"),
                TrainingCost.euros(1500.00),
                TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                TrainingHours.of(40),
                true,
                Set.of(CorporateKey.of("CK0002")),
                FiscalYear.of(2026),
                Instant.now());
        final var approved = initial.approve(
                CorporateKey.of("CK0099"),
                ManagerNotes.of("Approved!"),
                Instant.now());

        doNothing().when(approveUseCase).execute(any());
        when(getUseCase.execute(new GetTrainingRequestQuery(requestId))).thenReturn(Optional.of(approved));

        final var json = """
                {
                    "managerCorporateKey": "CK0099",
                    "managerNotes": "Approved!"
                }
                """;

        mockMvc.perform(post("/api/v1/training-requests/{id}/approve", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reviewedBy").value("CK0099"))
                .andExpect(jsonPath("$.managerNotes").value("Approved!"));
    }

    @Test
    @DisplayName("should reject training request and return 200 OK")
    void should_rejectTrainingRequest() throws Exception {
        final var requestId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        final var initial = TrainingRequest.create(
                TrainingRequestId.of(requestId),
                OuId.of(ouId),
                CorporateKey.of("CK0001"),
                TrainingName.of("Hexagonal Architecture"),
                TrainingCost.euros(1500.00),
                TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                TrainingHours.of(40),
                true,
                Set.of(CorporateKey.of("CK0002")),
                FiscalYear.of(2026),
                Instant.now());
        final var rejected = initial.reject(
                CorporateKey.of("CK0099"),
                RejectionReason.of("Budget exceeded"),
                ManagerNotes.of("Try next Q"),
                Instant.now());

        doNothing().when(rejectUseCase).execute(any());
        when(getUseCase.execute(new GetTrainingRequestQuery(requestId))).thenReturn(Optional.of(rejected));

        final var json = """
                {
                    "managerCorporateKey": "CK0099",
                    "rejectionReason": "Budget exceeded",
                    "managerNotes": "Try next Q"
                }
                """;

        mockMvc.perform(post("/api/v1/training-requests/{id}/reject", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId.toString()))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.reviewedBy").value("CK0099"))
                .andExpect(jsonPath("$.rejectionReason").value("Budget exceeded"))
                .andExpect(jsonPath("$.managerNotes").value("Try next Q"));
    }

    @Test
    @DisplayName("should get training request by ID")
    void should_getTrainingRequestById() throws Exception {
        final var requestId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        final var request = TrainingRequest.create(
                TrainingRequestId.of(requestId),
                OuId.of(ouId),
                CorporateKey.of("CK0001"),
                TrainingName.of("Hexagonal Architecture"),
                TrainingCost.euros(1500.00),
                TrainingPurpose.other("Custom workshop"),
                TrainingHours.of(40),
                true,
                Set.of(CorporateKey.of("CK0002")),
                FiscalYear.of(2026),
                Instant.now());

        when(getUseCase.execute(new GetTrainingRequestQuery(requestId))).thenReturn(Optional.of(request));

        mockMvc.perform(get("/api/v1/training-requests/{id}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId.toString()))
                .andExpect(jsonPath("$.purposeType").value("OTHER"))
                .andExpect(jsonPath("$.purposeCustomText").value("Custom workshop"));
    }

    @Test
    @DisplayName("should search training requests by OU ID and Status")
    void should_searchTrainingRequests() throws Exception {
        final var requestId = UUID.randomUUID();
        final var ouId = UUID.randomUUID();
        final var request = TrainingRequest.create(
                TrainingRequestId.of(requestId),
                OuId.of(ouId),
                CorporateKey.of("CK0001"),
                TrainingName.of("Hexagonal Architecture"),
                TrainingCost.euros(1500.00),
                TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                TrainingHours.of(40),
                true,
                Set.of(CorporateKey.of("CK0002")),
                FiscalYear.of(2026),
                Instant.now());

        when(searchUseCase.execute(any())).thenReturn(List.of(request));

        mockMvc.perform(get("/api/v1/training-requests")
                        .param("ouId", ouId.toString())
                        .param("status", "DRAFT")
                        .param("fiscalYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId.toString()))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }
}
