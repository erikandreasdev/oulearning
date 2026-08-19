package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HoursTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(ints = {1, 8, 40, 100})
        @DisplayName("should create Hours when positive integer provided")
        void should_createHours_when_positiveIntegerProvided(int val) {
            Hours hours = Hours.of(val);

            assertThat(hours.value()).isEqualTo(val);
            assertThat(hours.toString()).isEqualTo(String.valueOf(val));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -50})
        @DisplayName("should throw InvalidTrainingOperationException when hours non-positive")
        void should_throwException_when_hoursNonPositive(int invalidVal) {
            assertThatThrownBy(() -> Hours.of(invalidVal))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("strictly positive");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when values match")
        void should_beEqual_when_valuesMatch() {
            Hours h1 = Hours.of(40);
            Hours h2 = Hours.of(40);

            assertThat(h1).isEqualTo(h2);
            assertThat(h1.hashCode()).isEqualTo(h2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void should_notBeEqual_when_valuesDiffer() {
            Hours h1 = Hours.of(40);
            Hours h2 = Hours.of(20);

            assertThat(h1).isNotEqualTo(h2);
        }
    }
}
