package com.example.oulearning.organization.domain.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.DomainGivenProviders;
import com.example.oulearning.organization.domain.employee.exception.InvalidCorporateKeyException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(InstancioExtension.class)
class CorporateKeyTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest(name = "should reject: {0}")
        @ArgumentsSource(DomainGivenProviders.InvalidCorporateKeys.class)
        @DisplayName("should throw InvalidCorporateKeyException for invalid format")
        void should_throwException_when_formatIsInvalid(String invalidKey) {
            assertThatThrownBy(() -> CorporateKey.of(invalidKey))
                    .isInstanceOf(InvalidCorporateKeyException.class);
        }

        @Test
        @DisplayName("should throw InvalidCorporateKeyException when value is null")
        void should_throwException_when_valueIsNull() {
            assertThatThrownBy(() -> CorporateKey.of(null))
                    .isInstanceOf(InvalidCorporateKeyException.class)
                    .hasMessageContaining("CorporateKey cannot be null or blank");
        }

        @ParameterizedTest(name = "should accept {1}")
        @ArgumentsSource(DomainGivenProviders.ValidCorporateKeys.class)
        @DisplayName("should create CorporateKey for valid format")
        void should_createCorporateKey_when_formatIsValid(CorporateKey key, String expectedValue) {
            assertThat(key.value()).isEqualTo(expectedValue);
            assertThat(key.toString()).isEqualTo(expectedValue);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values match")
        void should_beEqual_when_valuesMatch() {
            final var key1 = CorporateKey.of("CK1234");
            final var key2 = CorporateKey.of("CK1234");

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            final var key1 = CorporateKey.of("CK0001");
            final var key2 = CorporateKey.of("CK0002");

            assertThat(key1).isNotEqualTo(key2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var key = DomainGenerators.randomCorporateKey();
            assertThat(key.getClass().isRecord()).isTrue();
        }
    }
}
