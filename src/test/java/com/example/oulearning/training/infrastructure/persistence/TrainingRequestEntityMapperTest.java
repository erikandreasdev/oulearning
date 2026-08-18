package com.example.oulearning.training.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.request.vo.identity.CorporateKey;
import com.example.oulearning.training.domain.request.vo.decision.ManagerNotes;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.vo.decision.RejectionReason;
import com.example.oulearning.training.domain.request.vo.details.TrainingCost;
import com.example.oulearning.training.domain.request.vo.details.TrainingHours;
import com.example.oulearning.training.domain.request.vo.details.TrainingName;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurpose;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurposeType;
import com.example.oulearning.training.domain.request.TrainingRequest;
import com.example.oulearning.training.domain.request.vo.identity.TrainingRequestId;
import com.example.oulearning.training.domain.request.vo.decision.TrainingRequestStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingRequestEntityMapperTest {

    private final TrainingRequestEntityMapper mapper = new TrainingRequestEntityMapper();

    @Nested
    @DisplayName("toEntity Mapping")
    class ToEntityMapping {

        @Test
        @DisplayName("should map domain TrainingRequest with review fields to TrainingRequestEntity")
        void should_mapDomainToEntity() {
            final var id = TrainingRequestId.random();
            final var ouId = OuId.of(UUID.randomUUID());
            final var requester = CorporateKey.of("CK0001");
            final var name = TrainingName.of("Kotlin & Spring Boot");
            final var cost = TrainingCost.euros(1200.00);
            final var purpose = TrainingPurpose.other("Custom Team Alignment");
            final var hours = TrainingHours.of(30);
            final var assistants = Set.of(CorporateKey.of("CK0002"));
            final var fy = FiscalYear.of(2026);
            final var now = Instant.now();

            final var domain = TrainingRequest.create(
                    id, ouId, requester, name, cost, purpose, hours, true, assistants, fy, now);
            final var rejected = domain.reject(
                    CorporateKey.of("CK0099"),
                    RejectionReason.of("Out of budget"),
                    ManagerNotes.of("Review next Q"),
                    now);

            final var entity = mapper.toEntity(rejected, 3L);

            assertThat(entity.id()).isEqualTo(id.toString());
            assertThat(entity.ouId()).isEqualTo(ouId.toString());
            assertThat(entity.requesterCorporateKey()).isEqualTo("CK0001");
            assertThat(entity.name()).isEqualTo("Kotlin & Spring Boot");
            assertThat(entity.costAmount()).isEqualByComparingTo(new BigDecimal("1200.00"));
            assertThat(entity.costCurrency()).isEqualTo("EUR");
            assertThat(entity.purposeType()).isEqualTo("OTHER");
            assertThat(entity.purposeCustomText()).isEqualTo("Custom Team Alignment");
            assertThat(entity.trainingHours()).isEqualTo(30);
            assertThat(entity.availableAtOrgUniversity()).isEqualTo(1);
            assertThat(entity.fiscalYear()).isEqualTo(2026);
            assertThat(entity.status()).isEqualTo("REJECTED");
            assertThat(entity.reviewedBy()).isEqualTo("CK0099");
            assertThat(entity.rejectionReason()).isEqualTo("Out of budget");
            assertThat(entity.managerNotes()).isEqualTo("Review next Q");
            assertThat(entity.reviewedAt()).isEqualTo(now);
            assertThat(entity.createdAt()).isEqualTo(now);
            assertThat(entity.version()).isEqualTo(3L);
        }

        @Test
        @DisplayName("should throw NullPointerException when domain model is null")
        void should_throw_whenDomainNull() {
            assertThatThrownBy(() -> mapper.toEntity(null, 0L))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toDomain Mapping")
    class ToDomainMapping {

        @Test
        @DisplayName("should map TrainingRequestEntity and assistant keys to domain aggregate")
        void should_mapEntityToDomain() {
            final var id = UUID.randomUUID().toString();
            final var ouId = UUID.randomUUID().toString();
            final var now = Instant.now();

            final var entity = new TrainingRequestEntity(
                    id, ouId, "CK0001", "Clean Code Workshop",
                    new BigDecimal("800.00"), "EUR",
                    "UPSKILLING", null, 16, 0, 2026,
                    "APPROVED", "CK0099", null, "Approved by director", now, now, 1L);

            final var assistants = Set.of("CK0005", "CK0006");

            final var domain = mapper.toDomain(entity, assistants);

            assertThat(domain.id().value()).isEqualTo(UUID.fromString(id));
            assertThat(domain.ouId().value()).isEqualTo(UUID.fromString(ouId));
            assertThat(domain.requester().value()).isEqualTo("CK0001");
            assertThat(domain.name().value()).isEqualTo("Clean Code Workshop");
            assertThat(domain.cost().amount()).isEqualByComparingTo(new BigDecimal("800.00"));
            assertThat(domain.purpose().type()).isEqualTo(TrainingPurposeType.UPSKILLING);
            assertThat(domain.hours().value()).isEqualTo(16);
            assertThat(domain.availableAtOrgUniversity()).isFalse();
            assertThat(domain.fiscalYear().value()).isEqualTo(2026);
            assertThat(domain.status()).isEqualTo(TrainingRequestStatus.APPROVED);
            assertThat(domain.optionalReviewedBy()).contains(CorporateKey.of("CK0099"));
            assertThat(domain.optionalManagerNotes().get().value()).isEqualTo("Approved by director");
            assertThat(domain.assistants()).containsExactlyInAnyOrder(
                    CorporateKey.of("CK0005"), CorporateKey.of("CK0006"));
        }
    }
}
