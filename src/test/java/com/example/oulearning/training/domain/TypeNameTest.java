package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TypeNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Power Skills", "  Technical  ", "Languages"})
        @DisplayName("should create TypeName when valid string provided")
        void should_createTypeName_when_validStringProvided(String raw) {
            TypeName name = TypeName.of(raw);

            assertThat(name.value()).isEqualTo(raw.strip());
            assertThat(name.toString()).isEqualTo(raw.strip());
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> new TypeName(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("should throw InvalidTrainingOperationException when name is blank")
        void should_throwException_when_nameIsBlank(String blank) {
            assertThatThrownBy(() -> new TypeName(blank))
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
            TypeName n1 = TypeName.of("Leadership");
            TypeName n2 = TypeName.of("Leadership");

            assertThat(n1).isEqualTo(n2);
            assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            TypeName n1 = TypeName.of("Leadership");
            TypeName n2 = TypeName.of("Onboarding");

            assertThat(n1).isNotEqualTo(n2);
        }
    }
}
