package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

@ExtendWith(InstancioExtension.class)
class CorporateKeyTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should create corporate key when valid dynamically generated key provided via InstancioSource")
        void should_createCorporateKey_when_generatedKeyProvided(
                @Given(DomainGivenProviders.ValidCorporateKeyProvider.class) final String rawKey) {
            // when
            final var key = CorporateKey.of(rawKey);

            // then
            assertThat(key.value()).isEqualTo(rawKey.strip().toUpperCase());
            assertThat(key.toString()).isEqualTo(rawKey.strip().toUpperCase());
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
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidCorporateKeyException when corporate key is blank via InstancioSource")
        void should_throwException_when_corporateKeyIsBlank(
                @Given(DomainGivenProviders.BlankStringProvider.class) final String blankKey) {
            // when / then
            assertThatThrownBy(() -> new CorporateKey(blankKey))
                    .isInstanceOf(InvalidCorporateKeyException.class)
                    .hasMessageContaining("cannot be blank");
        }

        @ParameterizedTest
        @InstancioSource(samples = 5)
        @DisplayName("should throw InvalidCorporateKeyException when format is invalid via InstancioSource")
        void should_throwException_when_formatIsInvalid(
                @Given(DomainGivenProviders.InvalidCorporateKeyProvider.class) final String invalidKey) {
            // when / then
            assertThatThrownBy(() -> CorporateKey.of(invalidKey))
                    .isInstanceOfSatisfying(
                            InvalidCorporateKeyException.class,
                            ex -> assertThat(ex.getInvalidValue()).isNotNull());
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when values match after normalization")
        void should_beEqual_when_valuesMatchAfterNormalization() {
            // given
            final var number = Instancio.gen().ints().range(0, 9999).get();
            final var key1 = CorporateKey.of("ck%04d".formatted(number));
            final var key2 = CorporateKey.of("CK%04d".formatted(number));

            // then
            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when corporate keys differ")
        void should_notBeEqual_when_keysDiffer() {
            // given
            final var key1 = CorporateKey.of("CK0001");
            final var key2 = CorporateKey.of("CK0002");

            // then
            assertThat(key1).isNotEqualTo(key2);
        }

        @Test
        @DisplayName("should maintain immutability and record semantics")
        void should_maintainImmutability() {
            // given
            final var key = DomainGenerators.randomCorporateKey();

            // then
            assertThat(key.getClass().isRecord()).isTrue();
            assertThat(key.value()).isEqualTo(key.toString());
        }
    }
}
