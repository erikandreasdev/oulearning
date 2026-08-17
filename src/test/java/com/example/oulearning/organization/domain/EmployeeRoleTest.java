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
class EmployeeRoleTest {

    @Nested
    @DisplayName("Parsing")
    class Parsing {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should parse dynamically generated valid role via InstancioSource")
        void should_parseRole_when_generatedEnumProvided(
                @Given(DomainGivenProviders.ValidEmployeeRoleProvider.class) final String rawValue) {
            final var parsed = EmployeeRole.parse(rawValue);
            assertThat(parsed.name()).isEqualTo(rawValue.strip().toUpperCase());
        }

        @Test
        @DisplayName("should throw InvalidEmployeeRoleException when role is null")
        void should_throwException_when_roleIsNull() {
            assertThatThrownBy(() -> EmployeeRole.parse(null))
                    .isInstanceOf(InvalidEmployeeRoleException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidEmployeeRoleException when role is blank via InstancioSource")
        void should_throwException_when_roleIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankValue) {
            assertThatThrownBy(() -> EmployeeRole.parse(blankValue))
                    .isInstanceOf(InvalidEmployeeRoleException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidEmployeeRoleException when role is unrecognized via InstancioSource")
        void should_throwException_when_roleIsUnrecognized(
                @Given(DomainGivenProviders.InvalidEmployeeRoleProvider.class) final String invalidRole) {
            assertThatThrownBy(() -> EmployeeRole.parse(invalidRole))
                    .isInstanceOfSatisfying(
                            InvalidEmployeeRoleException.class,
                            ex -> assertThat(ex.getInvalidValue()).isEqualTo(invalidRole));
        }
    }
}
