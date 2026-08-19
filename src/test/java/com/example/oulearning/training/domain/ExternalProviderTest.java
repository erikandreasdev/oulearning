package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExternalProviderTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid name and contact, when creating ExternalProvider, then create successfully")
        void givenValidNameAndContact_whenCreatingExternalProvider_thenCreateSuccessfully() {

            final var name = TrainingTestFactory.randomExternalProviderName();
            final var contact = TrainingTestFactory.randomExternalProviderContact();


            final var provider = ExternalProvider.of(name, contact);


            assertThat(provider.name()).isEqualTo(name);
            assertThat(provider.contact()).isEqualTo(contact);
        }

        @Test
        @DisplayName("given null name, when creating ExternalProvider, then throw InvalidTrainingOperationException")
        void givenNullName_whenCreatingExternalProvider_thenThrowInvalidTrainingOperationException() {

            final var contact = TrainingTestFactory.randomExternalProviderContact();




            assertThatThrownBy(() -> ExternalProvider.of(null, contact))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("ExternalProviderName cannot be null");
        }

        @Test
        @DisplayName("given null contact, when creating ExternalProvider, then throw InvalidTrainingOperationException")
        void givenNullContact_whenCreatingExternalProvider_thenThrowInvalidTrainingOperationException() {

            final var name = TrainingTestFactory.randomExternalProviderName();




            assertThatThrownBy(() -> ExternalProvider.of(name, null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("ExternalProviderContact cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical providers, when comparing, then they are equal")
        void givenIdenticalProviders_whenComparing_thenTheyAreEqual() {

            final var name = TrainingTestFactory.randomExternalProviderName();
            final var contact = TrainingTestFactory.randomExternalProviderContact();
            final var p1 = ExternalProvider.of(name, contact);
            final var p2 = ExternalProvider.of(name, contact);




            assertThat(p1).isEqualTo(p2);
            assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        }
    }
}
