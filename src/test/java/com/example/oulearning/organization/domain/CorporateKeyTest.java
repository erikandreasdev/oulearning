package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CorporateKeyTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @CsvSource({
            "'CK0001', 'CK0001'",
            "'CK1234', 'CK1234'",
            "'CK9999', 'CK9999'",
            "'ck1234', 'CK1234'",
            "'  ck0042  ', 'CK0042'"
        })
        @DisplayName("should create and normalize corporate key when valid format provided")
        void should_createAndNormalizeCorporateKey_when_validFormatProvided(
                String input, String expectedNormalized) {
            // when
            CorporateKey key = CorporateKey.of(input);

            // then
            assertThat(key.value()).isEqualTo(expectedNormalized);
            assertThat(key.toString()).isEqualTo(expectedNormalized);
        }

        @Test
        @DisplayName("should throw InvalidCorporateKeyException when corporate key is null")
        void should_throwException_when_corporateKeyIsNull() {
            // when / then
            assertThatThrownBy(() -> new CorporateKey(null))
                    .isInstanceOf(InvalidCorporateKeyException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("should throw InvalidCorporateKeyException when corporate key is blank")
        void should_throwException_when_corporateKeyIsBlank(String blankKey) {
            // when / then
            assertThatThrownBy(() -> new CorporateKey(blankKey))
                    .isInstanceOf(InvalidCorporateKeyException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "CK123", // 3 digits - too short
                    "CK12345", // 5 digits - too long
                    "AK1234", // wrong prefix
                    "1234CK", // wrong structure
                    "CKABCD", // letters instead of digits
                    "CK12A4", // mixed alphanumeric
                    "CK 1234", // space in between
                    "CK-1234" // hyphen in between
                })
        @DisplayName("should throw InvalidCorporateKeyException when format is invalid")
        void should_throwException_when_formatIsInvalid(String invalidKey) {
            // when / then
            assertThatThrownBy(() -> CorporateKey.of(invalidKey))
                    .isInstanceOfSatisfying(
                            InvalidCorporateKeyException.class,
                            ex -> assertThat(ex.getInvalidValue()).isNotNull());
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when values match after normalization")
        void should_beEqual_when_valuesMatchAfterNormalization() {
            // given
            CorporateKey key1 = CorporateKey.of("ck1234");
            CorporateKey key2 = CorporateKey.of("CK1234");

            // then
            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when corporate keys differ")
        void should_notBeEqual_when_keysDiffer() {
            // given
            CorporateKey key1 = CorporateKey.of("CK0001");
            CorporateKey key2 = CorporateKey.of("CK0002");

            // then
            assertThat(key1).isNotEqualTo(key2);
        }
    }
}
