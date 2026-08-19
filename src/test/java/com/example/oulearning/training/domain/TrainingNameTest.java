package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TrainingNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid training name string, when creating TrainingName, then create successfully")
        void givenValidTrainingNameString_whenCreatingTrainingName_thenCreateSuccessfully() {

            final var raw = TrainingTestFactory.randomTrainingNameString();


            final var name = TrainingName.of("  %s  ".formatted(raw));


            assertThat(name.value()).isEqualTo(raw);
            assertThat(name.toString()).isEqualTo(raw);
        }

        @Test
        @DisplayName("given null name, when creating TrainingName, then throw InvalidTrainingOperationException")
        void givenNullName_whenCreatingTrainingName_thenThrowInvalidTrainingOperationException() {





            assertThatThrownBy(() -> new TrainingName(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank name, when creating TrainingName, then throw InvalidTrainingOperationException")
        void givenBlankName_whenCreatingTrainingName_thenThrowInvalidTrainingOperationException(final String blank) {





            assertThatThrownBy(() -> new TrainingName(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given training name exceeding max length, when creating, then throw InvalidTrainingOperationException")
        void givenTrainingNameExceedingMaxLength_whenCreating_thenThrowInvalidTrainingOperationException() {

            final var longName = "A".repeat(TrainingConstants.MAX_NAME_LENGTH + 1);




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

            final var raw = TrainingTestFactory.randomTrainingNameString();
            final var n1 = TrainingName.of(raw);
            final var n2 = TrainingName.of(raw);




            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("given different training names, when comparing, then they are not equal")
        void givenDifferentTrainingNames_whenComparing_thenTheyAreNotEqual() {

            final var n1 = TrainingTestFactory.randomTrainingName();
            final var n2 = TrainingTestFactory.randomTrainingName();




            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
