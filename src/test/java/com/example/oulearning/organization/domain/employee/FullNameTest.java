package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FullNameTest {

    @Nested
    @DisplayName("Creation and Formatting")
    class CreationAndFormatting {

        @Test
        @DisplayName("given Name and Surname objects, when creating FullName, then create successfully")
        void givenNameAndSurnameObjects_whenCreatingFullName_thenCreateSuccessfully() {

            final var name = EmployeeTestFactory.randomName();
            final var surname = EmployeeTestFactory.randomSurname();


            final var fullName = FullName.of(name, surname);


            assertThat(fullName.name()).isEqualTo(name);
            assertThat(fullName.surname()).isEqualTo(surname);
            assertThat(fullName.formatted()).isEqualTo("%s %s".formatted(name.value(), surname.value()));
            assertThat(fullName.toString()).isEqualTo("%s %s".formatted(name.value(), surname.value()));
        }

        @Test
        @DisplayName("given raw strings with padding, when creating FullName, then trim and create successfully")
        void givenRawStringsWithPadding_whenCreatingFullName_thenTrimAndCreateSuccessfully() {

            final var rawName = EmployeeTestFactory.randomName().value();
            final var rawSurname = EmployeeTestFactory.randomSurname().value();


            final var fullName = FullName.of(" %s ".formatted(rawName), " %s ".formatted(rawSurname));


            assertThat(fullName.name().value()).isEqualTo(rawName);
            assertThat(fullName.surname().value()).isEqualTo(rawSurname);
            assertThat(fullName.formatted()).isEqualTo("%s %s".formatted(rawName, rawSurname));
        }

        @Test
        @DisplayName("given null name or surname, when creating FullName, then throw InvalidEmployeeException")
        void givenNullComponents_whenCreatingFullName_thenThrowInvalidEmployeeException() {

            final var surname = EmployeeTestFactory.randomSurname();
            final var name = EmployeeTestFactory.randomName();




            assertThatThrownBy(() -> new FullName(null, surname))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("First name cannot be null");

            assertThatThrownBy(() -> new FullName(name, null))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Surname cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        @DisplayName("given blank name or surname string, when creating, then throw InvalidEmployeeException")
        void givenBlankComponents_whenCreating_thenThrowInvalidEmployeeException(final String blank) {





            assertThatThrownBy(() -> Name.of(blank))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be blank");

            assertThatThrownBy(() -> Surname.of(blank))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @Test
        @DisplayName("given name or surname exceeding max length, when creating, then throw InvalidEmployeeException")
        void givenLengthExceeded_whenCreating_thenThrowInvalidEmployeeException() {

            final var longName = "A".repeat(51);




            assertThatThrownBy(() -> Name.of(longName))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Name length must be between");

            assertThatThrownBy(() -> Surname.of(longName))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Surname length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical names and surnames, when comparing FullName, then they are equal")
        void givenIdenticalNamesAndSurnames_whenComparingFullName_thenTheyAreEqual() {

            final var name = EmployeeTestFactory.randomName();
            final var surname = EmployeeTestFactory.randomSurname();
            final var fn1 = FullName.of(name, surname);
            final var fn2 = FullName.of(name, surname);




            assertThat(fn1).isEqualTo(fn2);
            assertThat(fn1.hashCode()).isEqualTo(fn2.hashCode());
        }

        @Test
        @DisplayName("given different names or surnames, when comparing FullName, then they are not equal")
        void givenDifferentNamesOrSurnames_whenComparingFullName_thenTheyAreNotEqual() {

            final var fn1 = EmployeeTestFactory.randomFullName();
            final var fn2 = EmployeeTestFactory.randomFullName();




            assertThat(fn1).isNotEqualTo(fn2);
        }
    }
}
