package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class EmployeeRoleTest {

    @Nested
    @DisplayName("Parsing")
    class Parsing {

        @ParameterizedTest
        @CsvSource({
            "'EMPLOYEE', EMPLOYEE",
            "'employee', EMPLOYEE",
            "'  Employee  ', EMPLOYEE",
            "'MANAGER', MANAGER",
            "'manager', MANAGER",
            "'TRAINER', TRAINER",
            "'trainer', TRAINER",
            "'ADMIN', ADMIN",
            "'admin', ADMIN"
        })
        @DisplayName("should parse valid role case-insensitively with whitespace trimmed")
        void should_parseRole_when_validStringProvided(String rawValue, EmployeeRole expectedRole) {
            EmployeeRole parsed = EmployeeRole.parse(rawValue);
            assertThat(parsed).isEqualTo(expectedRole);
        }

        @Test
        @DisplayName("should throw InvalidEmployeeRoleException when role is null")
        void should_throwException_when_roleIsNull() {
            assertThatThrownBy(() -> EmployeeRole.parse(null))
                    .isInstanceOf(InvalidEmployeeRoleException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidEmployeeRoleException when role is blank")
        void should_throwException_when_roleIsBlank(String blankValue) {
            assertThatThrownBy(() -> EmployeeRole.parse(blankValue))
                    .isInstanceOf(InvalidEmployeeRoleException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @ParameterizedTest
        @ValueSource(strings = {"UNKNOWN", "SUPERUSER", "DEVELOPER", "123"})
        @DisplayName("should throw InvalidEmployeeRoleException when role is unrecognized")
        void should_throwException_when_roleIsUnrecognized(String invalidRole) {
            assertThatThrownBy(() -> EmployeeRole.parse(invalidRole))
                    .isInstanceOfSatisfying(
                            InvalidEmployeeRoleException.class,
                            ex -> assertThat(ex.getInvalidValue()).isEqualTo(invalidRole));
        }
    }
}
