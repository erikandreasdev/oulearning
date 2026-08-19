package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TrainingPurposeTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create IDP purpose")
        void should_createIdpPurpose() {
            TrainingPurpose purpose = TrainingPurpose.idp();

            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.IDP);
            assertThat(purpose.optionalOtherPurpose()).isEmpty();
        }

        @Test
        @DisplayName("should create Department Goals purpose")
        void should_createDepartmentGoalsPurpose() {
            TrainingPurpose purpose = TrainingPurpose.departmentGoals();

            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.DEPARTMENT_GOALS);
            assertThat(purpose.optionalOtherPurpose()).isEmpty();
        }

        @Test
        @DisplayName("should create OTHER purpose with details")
        void should_createOtherPurpose_withDetails() {
            TrainingPurpose purpose = TrainingPurpose.other("Career transition preparation");

            assertThat(purpose.type()).isEqualTo(TrainingPurposeType.OTHER);
            assertThat(purpose.optionalOtherPurpose()).contains("Career transition preparation");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("should throw InvalidTrainingOperationException when OTHER purpose is blank")
        void should_throwException_when_otherPurposeIsBlank(String blank) {
            assertThatThrownBy(() -> TrainingPurpose.other(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Purpose description cannot be blank");
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when OTHER purpose is null")
        void should_throwException_when_otherPurposeIsNull() {
            assertThatThrownBy(() -> TrainingPurpose.other(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Purpose description cannot be blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when purpose type and details match")
        void should_beEqual_when_match() {
            TrainingPurpose p1 = TrainingPurpose.other("Certification");
            TrainingPurpose p2 = TrainingPurpose.other("Certification");

            assertThat(p1).isEqualTo(p2);
            assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when details differ")
        void should_notBeEqual_when_detailsDiffer() {
            TrainingPurpose p1 = TrainingPurpose.other("Certification A");
            TrainingPurpose p2 = TrainingPurpose.other("Certification B");

            assertThat(p1).isNotEqualTo(p2);
        }
    }
}
