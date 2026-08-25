package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ManagerReviewTest {

    private Modality modality;
    private Instant startDate;
    private Instant endDate;
    private ExternalProviderId providerId;
    private Instant now;

    @BeforeEach
    void setUp() {
        modality = TrainingTestFactory.randomModality();
        now = TrainingTestFactory.randomInstant();
        startDate = now.plus(10, ChronoUnit.DAYS);
        endDate = startDate.plus(2, ChronoUnit.DAYS);
        providerId = TrainingTestFactory.randomExternalProviderId();
    }

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid parameters, when creating ManagerReview, then create successfully")
        void givenValidParameters_whenCreatingManagerReview_thenCreateSuccessfully() {
            // given
            final var comments = TrainingTestFactory.randomComments();

            // when
            final var review = new ManagerReview(comments, modality, startDate, endDate, providerId, now);

            // then
            assertThat(review.comments()).isEqualTo(comments);
            assertThat(review.modality()).isEqualTo(modality);
            assertThat(review.startDate()).isEqualTo(startDate);
            assertThat(review.endDate()).isEqualTo(endDate);
            assertThat(review.optionalExternalProviderId()).contains(providerId);
            assertThat(review.reviewedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("given review without external provider, when creating ManagerReview, then optionalProviderId is empty")
        void givenReviewWithoutExternalProvider_whenCreatingManagerReview_thenOptionalProviderIdIsEmpty() {
            // given
            final var comments = TrainingTestFactory.randomComments();

            // when
            final var review = new ManagerReview(comments, modality, startDate, endDate, null, now);

            // then
            assertThat(review.optionalExternalProviderId()).isEmpty();
        }

        @Test
        @DisplayName("given null comments, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenNullComments_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new ManagerReview(null, modality, startDate, endDate, providerId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Comments cannot be null");
        }

        @Test
        @DisplayName("given blank comments, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenBlankComments_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> new ManagerReview(blank, modality, startDate, endDate, providerId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Comments cannot be blank");
        }

        @Test
        @DisplayName("given comments exceeding max length, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenCommentsExceedingMaxLength_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {
            // given
            final var longComments = "A".repeat(TrainingConstants.MAX_COMMENTS_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> new ManagerReview(longComments, modality, startDate, endDate, providerId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Comments length must be between");
        }

        @Test
        @DisplayName("given end date before start date, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenEndDateBeforeStartDate_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {
            // given
            final var invalidEnd = startDate.minus(1, ChronoUnit.DAYS);
            final var comments = TrainingTestFactory.randomComments();

            // when

            // then
            assertThatThrownBy(() -> new ManagerReview(comments, modality, startDate, invalidEnd, providerId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be before start date");
        }

        @Test
        @DisplayName("given null modality, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenNullModality_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {
            // given
            final var comments = TrainingTestFactory.randomComments();

            // when

            // then
            assertThatThrownBy(() -> new ManagerReview(comments, null, startDate, endDate, providerId, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Modality cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical reviews, when comparing, then they are equal")
        void givenIdenticalReviews_whenComparing_thenTheyAreEqual() {
            // given
            final var comments = TrainingTestFactory.randomComments();
            final var r1 = new ManagerReview(comments, modality, startDate, endDate, providerId, now);
            final var r2 = new ManagerReview(comments, modality, startDate, endDate, providerId, now);

            // when

            // then
            assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
        }
    }
}
