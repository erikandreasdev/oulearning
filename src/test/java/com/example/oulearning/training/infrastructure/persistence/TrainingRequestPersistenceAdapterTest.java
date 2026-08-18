package com.example.oulearning.training.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.request.vo.identity.CorporateKey;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.vo.details.TrainingCost;
import com.example.oulearning.training.domain.request.vo.details.TrainingHours;
import com.example.oulearning.training.domain.request.vo.details.TrainingName;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurpose;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurposeType;
import com.example.oulearning.training.domain.request.TrainingRequest;
import com.example.oulearning.training.domain.request.vo.identity.TrainingRequestId;
import com.example.oulearning.training.domain.request.vo.decision.TrainingRequestStatus;
import com.example.oulearning.training.domain.request.repository.TrainingRequestSearchCriteria;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

class TrainingRequestPersistenceAdapterTest {

    private TrainingRequestMyBatisMapper mapper;
    private TrainingRequestEntityMapper entityMapper;
    private TrainingRequestPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = mock(TrainingRequestMyBatisMapper.class);
        entityMapper = new TrainingRequestEntityMapper();
        adapter = new TrainingRequestPersistenceAdapter(mapper, entityMapper);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("should find TrainingRequest by ID")
        void should_findTrainingRequestById() {
            final var id = TrainingRequestId.random();
            final var ouId = OuId.of(UUID.randomUUID());
            final var now = Instant.now();

            final var entity = new TrainingRequestEntity(
                    id.toString(), ouId.toString(), "CK0001", "Spring Security Deep Dive",
                    new BigDecimal("1500.00"), "EUR", "CERTIFICATION", null, 24, 1, 2026,
                    "DRAFT", null, null, null, null, now, 0L);

            when(mapper.findTrainingRequestById(id.toString())).thenReturn(entity);
            when(mapper.findAssistantsByRequestId(id.toString())).thenReturn(Set.of("CK0002", "CK0003"));

            final var result = adapter.findById(id);

            assertThat(result).isPresent();
            final var tr = result.get();
            assertThat(tr.id()).isEqualTo(id);
            assertThat(tr.ouId()).isEqualTo(ouId);
            assertThat(tr.requester()).isEqualTo(CorporateKey.of("CK0001"));
            assertThat(tr.status()).isEqualTo(TrainingRequestStatus.DRAFT);
            assertThat(tr.assistants()).containsExactlyInAnyOrder(CorporateKey.of("CK0002"), CorporateKey.of("CK0003"));
        }

        @Test
        @DisplayName("should find TrainingRequests by criteria")
        void should_findByCriteria() {
            final var id = TrainingRequestId.random();
            final var ouId = OuId.of(UUID.randomUUID());
            final var now = Instant.now();

            final var entity = new TrainingRequestEntity(
                    id.toString(), ouId.toString(), "CK0001", "Spring Security Deep Dive",
                    new BigDecimal("1500.00"), "EUR", "CERTIFICATION", null, 24, 1, 2026,
                    "APPROVED", "CK0099", null, "Approved", now, now, 0L);

            when(mapper.findByCriteria(List.of(ouId.toString()), "APPROVED", 2026))
                    .thenReturn(List.of(entity));
            when(mapper.findAssistantsByRequestId(id.toString())).thenReturn(Set.of("CK0002"));

            final var list = adapter.findByCriteria(TrainingRequestSearchCriteria.of(
                    Set.of(ouId), TrainingRequestStatus.APPROVED, FiscalYear.of(2026)));

            assertThat(list).hasSize(1);
            assertThat(list.get(0).id()).isEqualTo(id);
            assertThat(list.get(0).status()).isEqualTo(TrainingRequestStatus.APPROVED);
        }
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        @DisplayName("should insert new TrainingRequest and its assistants")
        void should_insertTrainingRequest_when_doesNotExist() {
            final var request = TrainingRequest.create(
                    TrainingRequestId.random(),
                    OuId.of(UUID.randomUUID()),
                    CorporateKey.of("CK0001"),
                    TrainingName.of("DDD Workshop"),
                    TrainingCost.euros(1000.00),
                    TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                    TrainingHours.of(16),
                    true,
                    Set.of(CorporateKey.of("CK0002")),
                    FiscalYear.of(2026),
                    Instant.now());

            when(mapper.findTrainingRequestById(request.id().toString())).thenReturn(null);

            adapter.save(request);

            verify(mapper).insertTrainingRequest(any(TrainingRequestEntity.class));
            verify(mapper).insertAssistant(request.id().toString(), "CK0002");
        }

        @Test
        @DisplayName("should throw OptimisticLockingFailureException when update fails")
        void should_throwException_when_updateFails() {
            final var request = TrainingRequest.create(
                    TrainingRequestId.random(),
                    OuId.of(UUID.randomUUID()),
                    CorporateKey.of("CK0001"),
                    TrainingName.of("DDD Workshop"),
                    TrainingCost.euros(1000.00),
                    TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                    TrainingHours.of(16),
                    true,
                    Set.of(CorporateKey.of("CK0002")),
                    FiscalYear.of(2026),
                    Instant.now());

            final var existing = new TrainingRequestEntity(
                    request.id().toString(), request.ouId().toString(), "CK0001", "DDD Workshop",
                    new BigDecimal("1000.00"), "EUR", "UPSKILLING", null, 16, 1, 2026,
                    "DRAFT", null, null, null, null, Instant.now(), 1L);

            when(mapper.findTrainingRequestById(request.id().toString())).thenReturn(existing);
            when(mapper.updateTrainingRequest(any(TrainingRequestEntity.class))).thenReturn(0);

            assertThatThrownBy(() -> adapter.save(request))
                    .isInstanceOf(OptimisticLockingFailureException.class);
        }
    }
}
