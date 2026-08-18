package com.example.oulearning.organization.domain.employee.vo.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.employee.exception.identity.InvalidEmployeeRoleException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class EmployeeRoleTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidEmployeeRoles.class)
        @DisplayName("should throw InvalidEmployeeRoleException for invalid role string")
        void should_throwException_when_roleIsInvalid(String invalidRole) {
            assertThatThrownBy(() -> EmployeeRole.fromString(invalidRole))
                    .isInstanceOf(InvalidEmployeeRoleException.class);
        }

        @Test
        @DisplayName("should throw InvalidEmployeeRoleException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> EmployeeRole.fromString(null))
                    .isInstanceOf(InvalidEmployeeRoleException.class)
                    .hasMessageContaining("EmployeeRole cannot be null or blank");
        }

        @ParameterizedTest(name = "should parse {0} to {1}")
        @ArgumentsSource(DomainGivenProviders.ValidEmployeeRoles.class)
        @DisplayName("should parse EmployeeRole case-insensitively")
        void should_parseRole_when_valid(String input, EmployeeRole expected) {
            final var role = EmployeeRole.fromString(input);
            assertThat(role).isEqualTo(expected);
        }
    }
}
