package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FullNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create FullName when valid name and surname provided")
        void should_createFullName_when_validNameAndSurnameProvided() {
            FullName fullName = FullName.of("John", "Doe");

            assertThat(fullName.name().value()).isEqualTo("John");
            assertThat(fullName.surname().value()).isEqualTo("Doe");
            assertThat(fullName.formatted()).isEqualTo("John Doe");
            assertThat(fullName.toString()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> FullName.of((Name) null, new Surname("Doe")))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Name cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidEmployeeException when surname is null")
        void should_throwException_when_surnameIsNull() {
            assertThatThrownBy(() -> FullName.of(new Name("John"), (Surname) null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Surname cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when name and surname match")
        void should_beEqual_when_nameAndSurnameMatch() {
            FullName name1 = FullName.of("John", "Doe");
            FullName name2 = FullName.of("John", "Doe");

            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names differ")
        void should_notBeEqual_when_namesDiffer() {
            FullName name1 = FullName.of("John", "Doe");
            FullName name2 = FullName.of("Jane", "Doe");

            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
