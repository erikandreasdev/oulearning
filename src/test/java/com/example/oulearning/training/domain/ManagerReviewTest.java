package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ManagerReviewTest {

    private final Instant now = Instant.now();
    private final Instant startDate = now.plus(1, ChronoUnit.DAYS);
    private final Instant endDate = now.plus(3, ChronoUnit.DAYS);
    private final Modality modality = Modality.VIRTUAL;
    private final ExternalProvider provider = TrainingTestFactory.randomExternalProvider();

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid review data, when creating ManagerReview, then create successfully")
        void givenValidReviewData_whenCreatingManagerReview_thenCreateSuccessfully() {

            final var comments = TrainingTestFactory.randomComments();


            final var review = new ManagerReview(comments, modality, startDate, endDate, provider, now);


            assertThat(review.comments()).isEqualTo(comments);
            assertThat(review.modality()).isEqualTo(modality);
            assertThat(review.startDate()).isEqualTo(startDate);
            assertThat(review.endDate()).isEqualTo(endDate);
            assertThat(review.optionalExternalProvider()).contains(provider);
            assertThat(review.reviewedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("given review without external provider, when creating ManagerReview, then optionalProvider is empty")
        void givenReviewWithoutExternalProvider_whenCreatingManagerReview_thenOptionalProviderIsEmpty() {

            final var comments = TrainingTestFactory.randomComments();


            final var review = new ManagerReview(comments, modality, startDate, endDate, null, now);


            assertThat(review.optionalExternalProvider()).isEmpty();
        }

        @Test
        @DisplayName("given null comments, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenNullComments_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {





            assertThatThrownBy(() -> new ManagerReview(null, modality, startDate, endDate, provider, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Comments cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank comments, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenBlankComments_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException(final String blank) {





            assertThatThrownBy(() -> new ManagerReview(blank, modality, startDate, endDate, provider, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Comments cannot be blank");
        }

        @Test
        @DisplayName("given comments exceeding max length, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenCommentsExceedingMaxLength_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {

            final var longComments = "A".repeat(TrainingConstants.MAX_COMMENTS_LENGTH + 1);




            assertThatThrownBy(() -> new ManagerReview(longComments, modality, startDate, endDate, provider, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Comments length must be between");
        }

        @Test
        @DisplayName("given end date before start date, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenEndDateBeforeStartDate_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {

            final var invalidEnd = startDate.minus(1, ChronoUnit.DAYS);
            final var comments = TrainingTestFactory.randomComments();




            assertThatThrownBy(() -> new ManagerReview(comments, modality, startDate, invalidEnd, provider, now))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be before start date");
        }

        @Test
        @DisplayName("given null modality, when creating ManagerReview, then throw InvalidTrainingOperationException")
        void givenNullModality_whenCreatingManagerReview_thenThrowInvalidTrainingOperationException() {

            final var comments = TrainingTestFactory.randomComments();




            assertThatThrownBy(() -> new ManagerReview(comments, null, startDate, endDate, provider, now))
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

            final var comments = TrainingTestFactory.randomComments();
            final var r1 = new ManagerReview(comments, modality, startDate, endDate, provider, now);
            final var r2 = new ManagerReview(comments, modality, startDate, endDate, provider, now);




            assertThat(r1).isEqualTo(r2);
            assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        }
    }
}
