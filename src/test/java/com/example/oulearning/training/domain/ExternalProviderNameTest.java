package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExternalProviderNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid provider name, when creating ExternalProviderName, then create successfully")
        void givenValidProviderName_whenCreatingExternalProviderName_thenCreateSuccessfully() {

            final var nameString = TrainingTestFactory.randomTypeNameString();


            final var name = ExternalProviderName.of(nameString);


            assertThat(name.value()).isEqualTo(nameString);
            assertThat(name.toString()).isEqualTo(nameString);
        }

        @Test
        @DisplayName("given null provider name, when creating ExternalProviderName, then throw InvalidTrainingOperationException")
        void givenNullProviderName_whenCreatingExternalProviderName_thenThrowInvalidTrainingOperationException() {





            assertThatThrownBy(() -> ExternalProviderName.of(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("External provider name cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank provider name, when creating ExternalProviderName, then throw InvalidTrainingOperationException")
        void givenBlankProviderName_whenCreatingExternalProviderName_thenThrowInvalidTrainingOperationException(final String blank) {





            assertThatThrownBy(() -> ExternalProviderName.of(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("External provider name cannot be blank");
        }

        @Test
        @DisplayName("given provider name exceeding max length, when creating ExternalProviderName, then throw InvalidTrainingOperationException")
        void givenProviderNameExceedingMaxLength_whenCreatingExternalProviderName_thenThrowInvalidTrainingOperationException() {

            final var longName = "A".repeat(TrainingConstants.MAX_NAME_LENGTH + 1);




            assertThatThrownBy(() -> ExternalProviderName.of(longName))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("External provider name length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical provider names, when comparing, then they are equal")
        void givenIdenticalProviderNames_whenComparing_thenTheyAreEqual() {

            final var nameString = TrainingTestFactory.randomTypeNameString();
            final var n1 = ExternalProviderName.of(nameString);
            final var n2 = ExternalProviderName.of(nameString);




            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }
    }
}
