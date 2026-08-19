package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.Email;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ManagerReviewTest {

    private final Instant start = Instant.parse("2026-09-01T09:00:00Z");
    private final Instant end = Instant.parse("2026-09-03T17:00:00Z");
    private final Instant reviewedAt = Instant.parse("2026-08-20T10:00:00Z");
    private final ExternalProvider provider = ExternalProvider.of(
            ExternalProviderName.of("Tech Training Ltd"),
            ExternalProviderContact.of(Email.of("info@techtraining.com"), Phone.of("+1234567890")));

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create ManagerReview when valid parameters provided")
        void should_createManagerReview_when_validParams() {
            ManagerReview review = new ManagerReview(
                    "Approved for Q3 skills enhancement", Modality.VIRTUAL, start, end, provider, reviewedAt);

            assertThat(review.comments()).isEqualTo("Approved for Q3 skills enhancement");
            assertThat(review.modality()).isEqualTo(Modality.VIRTUAL);
            assertThat(review.startDate()).isEqualTo(start);
            assertThat(review.endDate()).isEqualTo(end);
            assertThat(review.optionalExternalProvider()).contains(provider);
            assertThat(review.reviewedAt()).isEqualTo(reviewedAt);
        }

        @Test
        @DisplayName("should create ManagerReview without external provider")
        void should_createManagerReview_withoutProvider() {
            ManagerReview review = new ManagerReview("Internal workshop", Modality.ON_SITE, start, end, null, reviewedAt);

            assertThat(review.optionalExternalProvider()).isEmpty();
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when end date is before start date")
        void should_throwException_when_endDateBeforeStartDate() {
            Instant invalidEnd = start.minusSeconds(3600);

            assertThatThrownBy(() -> new ManagerReview(
                            "Comments", Modality.BLENDED, start, invalidEnd, provider, reviewedAt))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be before start date");
        }

        @Test
        @DisplayName("should throw NullPointerException when required parameters are null")
        void should_throwException_when_requiredNull() {
            assertThatThrownBy(() -> new ManagerReview("Comments", null, start, end, provider, reviewedAt))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new ManagerReview("Comments", Modality.VIRTUAL, null, end, provider, reviewedAt))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new ManagerReview("Comments", Modality.VIRTUAL, start, null, provider, reviewedAt))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new ManagerReview("Comments", Modality.VIRTUAL, start, end, provider, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
