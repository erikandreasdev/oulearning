package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.exception.InvalidFullNameException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class FullNameTest {

    @Nested
    @DisplayName("Creation and Formatting")
    class CreationAndFormatting {

        @Test
        @DisplayName("should create FullName from Name and Surname")
        void should_createFullName_from_nameAndSurname() {
            final var name = Name.of("John");
            final var surname = Surname.of("Doe");
            final var fullName = FullName.of(name, surname);

            assertThat(fullName.name()).isEqualTo(name);
            assertThat(fullName.surname()).isEqualTo(surname);
            assertThat(fullName.formatted()).isEqualTo("John Doe");
            assertThat(fullName.toString()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should create FullName from string values")
        void should_createFullName_from_strings() {
            final var fullName = FullName.of("Jane", "Smith");

            assertThat(fullName.name().value()).isEqualTo("Jane");
            assertThat(fullName.surname().value()).isEqualTo("Smith");
            assertThat(fullName.formatted()).isEqualTo("Jane Smith");
        }

        @Test
        @DisplayName("should throw InvalidFullNameException when Name is null")
        void should_throwException_when_nameIsNull() {
            final var surname = Surname.of("Doe");

            assertThatThrownBy(() -> FullName.of(null, surname))
                    .isInstanceOf(InvalidFullNameException.class)
                    .hasMessageContaining("Name cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidFullNameException when Surname is null")
        void should_throwException_when_surnameIsNull() {
            final var name = Name.of("John");

            assertThatThrownBy(() -> FullName.of(name, null))
                    .isInstanceOf(InvalidFullNameException.class)
                    .hasMessageContaining("Surname cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when Name and Surname match")
        void should_beEqual_when_fieldsMatch() {
            final var fn1 = FullName.of("John", "Doe");
            final var fn2 = FullName.of("John", "Doe");

            assertThat(fn1).isEqualTo(fn2);
            assertThat(fn1.hashCode()).isEqualTo(fn2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when Name or Surname differ")
        void should_notBeEqual_when_fieldsDiffer() {
            final var fn1 = FullName.of("John", "Doe");
            final var fn2 = FullName.of("Jane", "Doe");

            assertThat(fn1).isNotEqualTo(fn2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var fullName = DomainGenerators.randomFullName();
            assertThat(fullName.getClass().isRecord()).isTrue();
        }
    }
}
