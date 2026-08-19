package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Engineering", "  Human Resources  ", "R&D Department"})
        @DisplayName("should create Name when valid value provided")
        void should_createName_when_validValueProvided(String rawValue) {
            Name name = Name.of(rawValue);

            assertThat(name.value()).isEqualTo(rawValue.strip());
            assertThat(name.toString()).isEqualTo(rawValue.strip());
        }

        @Test
        @DisplayName("should throw InvalidOuException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> new Name(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("should throw InvalidOuException when name is blank")
        void should_throwException_when_nameIsBlank(String blankValue) {
            assertThatThrownBy(() -> new Name(blankValue))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when names match")
        void should_beEqual_when_namesMatch() {
            Name name1 = Name.of("Engineering");
            Name name2 = Name.of("Engineering");

            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            Name name1 = Name.of("Engineering");
            Name name2 = Name.of("Marketing");

            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
