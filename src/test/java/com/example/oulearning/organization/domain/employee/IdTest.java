package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(strings = {"EMP-12345", "  WORKDAY_987  ", "USER_01"})
        @DisplayName("should create employee id when valid value provided")
        void should_createId_when_validValueProvided(String rawValue) {
            Id id = Id.of(rawValue);

            assertThat(id.value()).isEqualTo(rawValue.strip());
            assertThat(id.toString()).isEqualTo(rawValue.strip());
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when id is null")
        void should_throwException_when_idIsNull() {
            assertThatThrownBy(() -> new Id(null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("should throw InvalidEmployeeException when id is blank")
        void should_throwException_when_idIsBlank(String blankValue) {
            assertThatThrownBy(() -> new Id(blankValue))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Id id1 = Id.of("EMP001");
            Id id2 = Id.of("EMP001");

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Id id1 = Id.of("EMP001");
            Id id2 = Id.of("EMP002");

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
