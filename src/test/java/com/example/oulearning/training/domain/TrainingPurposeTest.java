package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingPurposeTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given IDP purpose, when creating TrainingPurpose, then create successfully with no description")
        void givenIdpPurpose_whenCreatingTrainingPurpose_thenCreateSuccessfullyWithNoDescription() {
            // given

            // when
            final var purpose = TrainingPurpose.idp();

            // then
            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.IDP);
            assertThat(purpose.otherPurpose()).isNull();
            assertThat(purpose.optionalOtherPurpose()).isEmpty();
        }

        @Test
        @DisplayName("given Department Goals purpose, when creating TrainingPurpose, then create successfully with no description")
        void givenDepartmentGoalsPurpose_whenCreatingTrainingPurpose_thenCreateSuccessfullyWithNoDescription() {
            // given

            // when
            final var purpose = TrainingPurpose.departmentGoals();

            // then
            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.DEPARTMENT_GOALS);
            assertThat(purpose.otherPurpose()).isNull();
            assertThat(purpose.optionalOtherPurpose()).isEmpty();
        }

        @Test
        @DisplayName("given OTHER purpose with valid text, when creating TrainingPurpose, then create successfully with description")
        void givenOtherPurposeWithValidText_whenCreatingTrainingPurpose_thenCreateSuccessfullyWithDescription() {
            // given
            final var text = TrainingTestFactory.randomPurposeDescription();

            // when
            final var purpose = TrainingPurpose.other(text);

            // then
            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.OTHER);
            assertThat(purpose.otherPurpose()).isEqualTo(text);
            assertThat(purpose.optionalOtherPurpose()).contains(text);
        }

        @Test
        @DisplayName("given OTHER purpose with blank text, when creating TrainingPurpose, then throw InvalidTrainingOperationException")
        void givenOtherPurposeWithBlankText_whenCreatingTrainingPurpose_thenThrowInvalidTrainingOperationException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> TrainingPurpose.other(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given null text for OTHER purpose, when creating TrainingPurpose, then throw InvalidTrainingOperationException")
        void givenNullTextForOtherPurpose_whenCreatingTrainingPurpose_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> TrainingPurpose.other(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given OTHER purpose exceeding max length, when creating TrainingPurpose, then throw InvalidTrainingOperationException")
        void givenOtherPurposeExceedingMaxLength_whenCreatingTrainingPurpose_thenThrowInvalidTrainingOperationException() {
            // given
            final var longText = "A".repeat(TrainingConstants.MAX_PURPOSE_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> TrainingPurpose.other(longText))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Purpose description length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical purposes, when comparing TrainingPurpose, then they are equal")
        void givenIdenticalPurposes_whenComparingTrainingPurpose_thenTheyAreEqual() {
            // given
            final var p1 = TrainingPurpose.idp();
            final var p2 = TrainingPurpose.idp();

            // when

            // then
            assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
        }

        @Test
        @DisplayName("given different purposes, when comparing TrainingPurpose, then they are not equal")
        void givenDifferentPurposes_whenComparingTrainingPurpose_thenTheyAreNotEqual() {
            // given
            final var p1 = TrainingPurpose.idp();
            final var p2 = TrainingPurpose.departmentGoals();

            // when

            // then
            assertThat(p1).isNotEqualTo(p2);
        }
    }
}
