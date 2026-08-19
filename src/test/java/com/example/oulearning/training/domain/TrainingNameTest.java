package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TrainingNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Clean Architecture Workshop", "  DDD in Practice  ", "Java 21 Mastery"})
        @DisplayName("should create TrainingName when valid string provided")
        void should_createTrainingName_when_validStringProvided(String raw) {
            TrainingName name = TrainingName.of(raw);

            assertThat(name.value()).isEqualTo(raw.strip());
            assertThat(name.toString()).isEqualTo(raw.strip());
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> new TrainingName(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("should throw InvalidTrainingOperationException when name is blank")
        void should_throwException_when_nameIsBlank(String blank) {
            assertThatThrownBy(() -> new TrainingName(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when names match")
        void should_beEqual_when_namesMatch() {
            TrainingName n1 = TrainingName.of("Clean Code");
            TrainingName n2 = TrainingName.of("Clean Code");

            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            TrainingName n1 = TrainingName.of("Clean Code");
            TrainingName n2 = TrainingName.of("Refactoring");

            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
