package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FullNameTest {

    @Nested
    @DisplayName("Creation and Formatting")
    class CreationAndFormatting {

        @Test
        @DisplayName("should create FullName from typed Name and Surname")
        void should_createFullName_from_typedObjects() {
            // given
            Name name = Name.of("John");
            Surname surname = Surname.of("Doe");

            // when
            FullName fullName = FullName.of(name, surname);

            // then
            assertThat(fullName.name()).isEqualTo(name);
            assertThat(fullName.surname()).isEqualTo(surname);
            assertThat(fullName.formatted()).isEqualTo("John Doe");
            assertThat(fullName.toString()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should create FullName from raw strings")
        void should_createFullName_from_rawStrings() {
            // when
            FullName fullName = FullName.of("Mary-Jane", "Watson");

            // then
            assertThat(fullName.formatted()).isEqualTo("Mary-Jane Watson");
            assertThat(fullName.toString()).isEqualTo("Mary-Jane Watson");
        }

        @Test
        @DisplayName("should throw InvalidNameException when name is null")
        void should_throwException_when_nameIsNull() {
            // when / then
            assertThatThrownBy(() -> new FullName(null, Surname.of("Doe")))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidSurnameException when surname is null")
        void should_throwException_when_surnameIsNull() {
            // when / then
            assertThatThrownBy(() -> new FullName(Name.of("John"), null))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when name and surname are equal")
        void should_beEqual_when_nameAndSurnameAreEqual() {
            // given
            FullName fullName1 = FullName.of("John", "Doe");
            FullName fullName2 = FullName.of("John", "Doe");

            // then
            assertThat(fullName1).isEqualTo(fullName2);
            assertThat(fullName1.hashCode()).isEqualTo(fullName2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names or surnames differ")
        void should_notBeEqual_when_namesOrSurnamesDiffer() {
            // given
            FullName fullName1 = FullName.of("John", "Doe");
            FullName fullName2 = FullName.of("Jane", "Doe");

            // then
            assertThat(fullName1).isNotEqualTo(fullName2);
        }
    }
}
