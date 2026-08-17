package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

@ExtendWith(InstancioExtension.class)
class FullNameTest {

    @Nested
    @DisplayName("Creation and Formatting")
    class CreationAndFormatting {

        @Test
        @DisplayName("should create FullName from typed Name and Surname")
        void should_createFullName_from_typedObjects() {
            // given
            final var name = DomainGenerators.randomName();
            final var surname = DomainGenerators.randomSurname();

            // when
            final var fullName = FullName.of(name, surname);

            // then
            assertThat(fullName.name()).isEqualTo(name);
            assertThat(fullName.surname()).isEqualTo(surname);
            assertThat(fullName.formatted()).isEqualTo("%s %s".formatted(name.value(), surname.value()));
            assertThat(fullName.toString()).isEqualTo("%s %s".formatted(name.value(), surname.value()));
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create FullName from raw strings via InstancioSource")
        void should_createFullName_from_rawStrings(
                @Given(DomainGivenProviders.ValidNameProvider.class) final String nameStr,
                @Given(DomainGivenProviders.ValidSurnameProvider.class) final String surnameStr) {
            // when
            final var fullName = FullName.of(nameStr, surnameStr);

            // then
            assertThat(fullName.formatted()).isEqualTo("%s %s".formatted(nameStr, surnameStr));
            assertThat(fullName.toString()).isEqualTo("%s %s".formatted(nameStr, surnameStr));
        }

        @Test
        @DisplayName("should throw InvalidNameException when name is null")
        void should_throwException_when_nameIsNull() {
            final var surname = DomainGenerators.randomSurname();
            assertThatThrownBy(() -> new FullName(null, surname))
                    .isInstanceOf(InvalidNameException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidSurnameException when surname is null")
        void should_throwException_when_surnameIsNull() {
            final var name = DomainGenerators.randomName();
            assertThatThrownBy(() -> new FullName(name, null))
                    .isInstanceOf(InvalidSurnameException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when name and surname are equal")
        void should_beEqual_when_nameAndSurnameAreEqual() {
            // given
            final var name = DomainGenerators.randomName();
            final var surname = DomainGenerators.randomSurname();
            final var fullName1 = FullName.of(name, surname);
            final var fullName2 = FullName.of(name, surname);

            // then
            assertThat(fullName1).isEqualTo(fullName2);
            assertThat(fullName1.hashCode()).isEqualTo(fullName2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when names or surnames differ")
        void should_notBeEqual_when_namesOrSurnamesDiffer() {
            // given
            final var fullName1 = DomainGenerators.randomFullName();
            final var fullName2 = DomainGenerators.randomFullName();

            // then
            assertThat(fullName1).isNotEqualTo(fullName2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var fullName = DomainGenerators.randomFullName();

            // then
            assertThat(fullName.getClass().isRecord()).isTrue();
            assertThat(fullName.formatted()).isEqualTo(fullName.toString());
        }
    }
}
