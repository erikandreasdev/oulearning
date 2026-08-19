package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TrainingNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid training name string, when creating TrainingName, then create successfully")
        void givenValidTrainingNameString_whenCreatingTrainingName_thenCreateSuccessfully() {
            // given
            final var raw = TrainingTestFactory.randomTrainingNameString();

            // when
            final var name = TrainingName.of("  %s  ".formatted(raw));

            // then
            assertThat(name.value()).isEqualTo(raw);
            assertThat(name.toString()).isEqualTo(raw);
        }

        @Test
        @DisplayName("given null name, when creating TrainingName, then throw InvalidTrainingOperationException")
        void givenNullName_whenCreatingTrainingName_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new TrainingName(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank name, when creating TrainingName, then throw InvalidTrainingOperationException")
        void givenBlankName_whenCreatingTrainingName_thenThrowInvalidTrainingOperationException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> new TrainingName(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given training name exceeding max length, when creating, then throw InvalidTrainingOperationException")
        void givenTrainingNameExceedingMaxLength_whenCreating_thenThrowInvalidTrainingOperationException() {
            // given
            final var longName = "A".repeat(TrainingConstants.MAX_NAME_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> new TrainingName(longName))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Training name length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical training names, when comparing, then they are equal")
        void givenIdenticalTrainingNames_whenComparing_thenTheyAreEqual() {
            // given
            final var raw = TrainingTestFactory.randomTrainingNameString();
            final var n1 = TrainingName.of(raw);
            final var n2 = TrainingName.of(raw);

            // when

            // then
            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("given different training names, when comparing, then they are not equal")
        void givenDifferentTrainingNames_whenComparing_thenTheyAreNotEqual() {
            // given
            final var n1 = TrainingTestFactory.randomTrainingName();
            final var n2 = TrainingTestFactory.randomTrainingName();

            // when

            // then
            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
