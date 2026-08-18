package com.example.oulearning.training.domain.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.request.exception.IllegalTrainingRequestStateException;
import com.example.oulearning.training.domain.request.exception.InvalidRejectionReasonException;
import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;
import com.example.oulearning.training.domain.request.exception.UnauthorizedManagerException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.example.oulearning.training.domain.request.vo.identity.CorporateKey;
import com.example.oulearning.training.domain.request.vo.decision.ManagerNotes;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.vo.decision.RejectionReason;
import com.example.oulearning.training.domain.request.vo.details.TrainingCost;
import com.example.oulearning.training.domain.request.vo.details.TrainingHours;
import com.example.oulearning.training.domain.request.vo.details.TrainingName;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurpose;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurposeType;
import com.example.oulearning.training.domain.request.vo.identity.TrainingRequestId;
import com.example.oulearning.training.domain.request.vo.decision.TrainingRequestStatus;

class TrainingRequestTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create TrainingRequest with initial DRAFT status")
        void should_createTrainingRequest_whenValid() {
            final var id = TrainingRequestId.random();
            final var ouId = OuId.of(UUID.randomUUID());
            final var requester = CorporateKey.of("CK0001");
            final var name = TrainingName.of("Advanced DDD & Clean Architecture");
            final var cost = TrainingCost.euros(1500.00);
            final var purpose = TrainingPurpose.of(TrainingPurposeType.UPSKILLING);
            final var hours = TrainingHours.of(40);
            final var assistants = Set.of(CorporateKey.of("CK0002"), CorporateKey.of("CK0003"));
            final var fy = FiscalYear.of(2026);
            final var now = Instant.now();

            final var request = TrainingRequest.create(
                    id,
                    ouId,
                    requester,
                    name,
                    cost,
                    purpose,
                    hours,
                    true,
                    assistants,
                    fy,
                    now);

            assertThat(request.id()).isEqualTo(id);
            assertThat(request.ouId()).isEqualTo(ouId);
            assertThat(request.requester()).isEqualTo(requester);
            assertThat(request.name().value()).isEqualTo("Advanced DDD & Clean Architecture");
            assertThat(request.cost().amount()).isEqualByComparingTo(new BigDecimal("1500.00"));
            assertThat(request.cost().currency()).isEqualTo("EUR");
            assertThat(request.purpose().type()).isEqualTo(TrainingPurposeType.UPSKILLING);
            assertThat(request.purpose().customTextOptional()).isEmpty();
            assertThat(request.hours().value()).isEqualTo(40);
            assertThat(request.availableAtOrgUniversity()).isTrue();
            assertThat(request.assistants()).containsExactlyInAnyOrder(CorporateKey.of("CK0002"), CorporateKey.of("CK0003"));
            assertThat(request.fiscalYear()).isEqualTo(fy);
            assertThat(request.status()).isEqualTo(TrainingRequestStatus.DRAFT);
            assertThat(request.optionalReviewedBy()).isEmpty();
            assertThat(request.optionalRejectionReason()).isEmpty();
            assertThat(request.optionalManagerNotes()).isEmpty();
            assertThat(request.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("should create TrainingRequest with OTHER purpose and custom text")
        void should_createTrainingRequest_withOtherPurpose() {
            final var purpose = TrainingPurpose.other("Specialized team alignment workshop");

            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.OTHER);
            assertThat(purpose.customTextOptional()).contains("Specialized team alignment workshop");
        }

        @Test
        @DisplayName("should throw InvalidTrainingRequestException when OTHER purpose lacks custom text")
        void should_throw_whenOtherPurposeLacksCustomText() {
            assertThatThrownBy(() -> TrainingPurpose.other(null))
                    .isInstanceOf(InvalidTrainingRequestException.class)
                    .hasMessageContaining("Custom purpose text is required");

            assertThatThrownBy(() -> TrainingPurpose.other("   "))
                    .isInstanceOf(InvalidTrainingRequestException.class)
                    .hasMessageContaining("Custom purpose text is required");
        }

        @Test
        @DisplayName("should throw InvalidTrainingRequestException when assistants list is empty or null")
        void should_throw_whenAssistantsEmptyOrNull() {
            final var id = TrainingRequestId.random();
            final var ouId = OuId.of(UUID.randomUUID());
            final var requester = CorporateKey.of("CK0001");
            final var name = TrainingName.of("Cloud Native");
            final var cost = TrainingCost.euros(500);
            final var purpose = TrainingPurpose.of(TrainingPurposeType.CERTIFICATION);
            final var hours = TrainingHours.of(16);
            final var fy = FiscalYear.of(2026);
            final var now = Instant.now();

            assertThatThrownBy(() -> TrainingRequest.create(
                    id, ouId, requester, name, cost, purpose, hours, false, null, fy, now))
                    .isInstanceOf(InvalidTrainingRequestException.class)
                    .hasMessageContaining("Assistants list cannot be null or empty");

            assertThatThrownBy(() -> TrainingRequest.create(
                    id, ouId, requester, name, cost, purpose, hours, false, Set.of(), fy, now))
                    .isInstanceOf(InvalidTrainingRequestException.class)
                    .hasMessageContaining("Assistants list cannot be null or empty");
        }

        @Test
        @DisplayName("should throw InvalidTrainingRequestException when hours <= 0")
        void should_throw_whenHoursInvalid() {
            assertThatThrownBy(() -> TrainingHours.of(0))
                    .isInstanceOf(InvalidTrainingRequestException.class);
            assertThatThrownBy(() -> TrainingHours.of(-5))
                    .isInstanceOf(InvalidTrainingRequestException.class);
        }

        @Test
        @DisplayName("should throw InvalidTrainingRequestException when cost is negative")
        void should_throw_whenCostNegative() {
            assertThatThrownBy(() -> TrainingCost.euros(-10))
                    .isInstanceOf(InvalidTrainingRequestException.class);
        }
    }

    @Nested
    @DisplayName("Manager Approval and Rejection Lifecycle")
    class ManagerReviewLifecycle {

        @Test
        @DisplayName("should transition from DRAFT to APPROVED upon manager approval")
        void should_approveTrainingRequest() {
            final var request = createDraftRequest();
            final var manager = CorporateKey.of("CK0099");
            final var notes = ManagerNotes.of("Approved for Q3 engineering budget");
            final var approvedAt = Instant.now();

            final var approved = request.approve(manager, notes, approvedAt);

            assertThat(approved.status()).isEqualTo(TrainingRequestStatus.APPROVED);
            assertThat(approved.optionalReviewedBy()).contains(manager);
            assertThat(approved.optionalManagerNotes()).contains(notes);
            assertThat(approved.optionalReviewedAt()).contains(approvedAt);
            assertThat(approved.optionalRejectionReason()).isEmpty();
        }

        @Test
        @DisplayName("should transition from DRAFT to REJECTED with mandatory rejection reason")
        void should_rejectTrainingRequest() {
            final var request = createDraftRequest();
            final var manager = CorporateKey.of("CK0099");
            final var reason = RejectionReason.of("Budget exceeded for this quarter");
            final var notes = ManagerNotes.of("Please submit again in Q4");
            final var rejectedAt = Instant.now();

            final var rejected = request.reject(manager, reason, notes, rejectedAt);

            assertThat(rejected.status()).isEqualTo(TrainingRequestStatus.REJECTED);
            assertThat(rejected.optionalReviewedBy()).contains(manager);
            assertThat(rejected.optionalRejectionReason()).contains(reason);
            assertThat(rejected.optionalManagerNotes()).contains(notes);
            assertThat(rejected.optionalReviewedAt()).contains(rejectedAt);
        }

        @Test
        @DisplayName("should throw InvalidRejectionReasonException when rejecting without reason")
        void should_throw_whenRejectingWithoutReason() {
            final var request = createDraftRequest();
            final var manager = CorporateKey.of("CK0099");

            assertThatThrownBy(() -> request.reject(manager, null, null, Instant.now()))
                    .isInstanceOf(InvalidRejectionReasonException.class);
        }

        @Test
        @DisplayName("should throw IllegalTrainingRequestStateException when approving an already approved or rejected request")
        void should_throw_whenApprovingNonDraftRequest() {
            final var request = createDraftRequest();
            final var manager = CorporateKey.of("CK0099");
            final var approved = request.approve(manager, null, Instant.now());

            assertThatThrownBy(() -> approved.approve(manager, null, Instant.now()))
                    .isInstanceOf(IllegalTrainingRequestStateException.class)
                    .hasMessageContaining("must be DRAFT");

            final var rejected = request.reject(manager, RejectionReason.of("Reason"), null, Instant.now());
            assertThatThrownBy(() -> rejected.approve(manager, null, Instant.now()))
                    .isInstanceOf(IllegalTrainingRequestStateException.class)
                    .hasMessageContaining("must be DRAFT");
        }

        @Test
        @DisplayName("should throw UnauthorizedManagerException when manager key is null")
        void should_throw_whenManagerKeyNull() {
            final var request = createDraftRequest();

            assertThatThrownBy(() -> request.approve(null, null, Instant.now()))
                    .isInstanceOf(UnauthorizedManagerException.class);

            assertThatThrownBy(() -> request.reject(null, RejectionReason.of("Reason"), null, Instant.now()))
                    .isInstanceOf(UnauthorizedManagerException.class);
        }

        private TrainingRequest createDraftRequest() {
            return TrainingRequest.create(
                    TrainingRequestId.random(),
                    OuId.of(UUID.randomUUID()),
                    CorporateKey.of("CK0001"),
                    TrainingName.of("Clean Code Workshop"),
                    TrainingCost.euros(1000.00),
                    TrainingPurpose.of(TrainingPurposeType.UPSKILLING),
                    TrainingHours.of(20),
                    true,
                    Set.of(CorporateKey.of("CK0002")),
                    FiscalYear.of(2026),
                    Instant.now());
        }
    }
}
