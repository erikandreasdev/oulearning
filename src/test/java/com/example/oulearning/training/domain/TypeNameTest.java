package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TypeNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid type name string, when creating TypeName, then create successfully")
        void givenValidTypeNameString_whenCreatingTypeName_thenCreateSuccessfully() {
            // given
            final var raw = TrainingTestFactory.randomTypeNameString();

            // when
            final var name = TypeName.of("  %s  ".formatted(raw));

            // then
            assertThat(name.value()).isEqualTo(raw);
            assertThat(name.toString()).isEqualTo(raw);
        }

        @Test
        @DisplayName("given null name, when creating TypeName, then throw InvalidTrainingOperationException")
        void givenNullName_whenCreatingTypeName_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new TypeName(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank name, when creating TypeName, then throw InvalidTrainingOperationException")
        void givenBlankName_whenCreatingTypeName_thenThrowInvalidTrainingOperationException(final String blank) {
            // given

            // when

            // then
            assertThatThrownBy(() -> new TypeName(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given type name exceeding max length, when creating, then throw InvalidTrainingOperationException")
        void givenTypeNameExceedingMaxLength_whenCreating_thenThrowInvalidTrainingOperationException() {
            // given
            final var longName = "A".repeat(TrainingConstants.MAX_NAME_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> new TypeName(longName))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("TypeName length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical type names, when comparing, then they are equal")
        void givenIdenticalTypeNames_whenComparing_thenTheyAreEqual() {
            // given
            final var raw = TrainingTestFactory.randomTypeNameString();
            final var n1 = TypeName.of(raw);
            final var n2 = TypeName.of(raw);

            // when

            // then
            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("given different type names, when comparing, then they are not equal")
        void givenDifferentTypeNames_whenComparing_thenTheyAreNotEqual() {
            // given
            final var n1 = TrainingTestFactory.randomTypeName();
            final var n2 = TrainingTestFactory.randomTypeName();

            // when

            // then
            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
