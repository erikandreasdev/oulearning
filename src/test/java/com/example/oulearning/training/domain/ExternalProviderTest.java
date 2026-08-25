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
        @DisplayName("given valid id, name and contact, when creating ExternalProvider, then create successfully")
        void givenValidIdNameAndContact_whenCreatingExternalProvider_thenCreateSuccessfully() {
            // given
            final var id = TrainingTestFactory.randomExternalProviderId();
            final var name = TrainingTestFactory.randomExternalProviderName();
            final var contact = TrainingTestFactory.randomExternalProviderContact();

            // when
            final var provider = ExternalProvider.create(id, name, contact);

            // then
            assertThat(provider.id()).isEqualTo(id);
            assertThat(provider.name()).isEqualTo(name);
            assertThat(provider.contact()).isEqualTo(contact);
            assertThat(provider.active()).isTrue();
        }

        @Test
        @DisplayName("given null id, when creating ExternalProvider, then throw InvalidTrainingOperationException")
        void givenNullId_whenCreatingExternalProvider_thenThrowInvalidTrainingOperationException() {
            // given
            final var name = TrainingTestFactory.randomExternalProviderName();
            final var contact = TrainingTestFactory.randomExternalProviderContact();

            // when

            // then
            assertThatThrownBy(() -> ExternalProvider.create(null, name, contact))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("ExternalProviderId cannot be null");
        }

        @Test
        @DisplayName("given null name, when creating ExternalProvider, then throw InvalidTrainingOperationException")
        void givenNullName_whenCreatingExternalProvider_thenThrowInvalidTrainingOperationException() {
            // given
            final var id = TrainingTestFactory.randomExternalProviderId();
            final var contact = TrainingTestFactory.randomExternalProviderContact();

            // when

            // then
            assertThatThrownBy(() -> ExternalProvider.create(id, null, contact))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("ExternalProviderName cannot be null");
        }

        @Test
        @DisplayName("given null contact, when creating ExternalProvider, then throw InvalidTrainingOperationException")
        void givenNullContact_whenCreatingExternalProvider_thenThrowInvalidTrainingOperationException() {
            // given
            final var id = TrainingTestFactory.randomExternalProviderId();
            final var name = TrainingTestFactory.randomExternalProviderName();

            // when

            // then
            assertThatThrownBy(() -> ExternalProvider.create(id, name, null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("ExternalProviderContact cannot be null");
        }
    }

    @Nested
    @DisplayName("Mutations and Lifecycle")
    class MutationsAndLifecycle {

        @Test
        @DisplayName("given existing provider, when updating name and contact, then return updated provider")
        void givenExistingProvider_whenUpdatingNameAndContact_thenReturnUpdatedProvider() {
            // given
            final var provider = TrainingTestFactory.randomExternalProvider();
            final var newName = TrainingTestFactory.randomExternalProviderName();
            final var newContact = TrainingTestFactory.randomExternalProviderContact();

            // when
            final var updated = provider.update(newName, newContact);

            // then
            assertThat(updated.id()).isEqualTo(provider.id());
            assertThat(updated.name()).isEqualTo(newName);
            assertThat(updated.contact()).isEqualTo(newContact);
            assertThat(updated.active()).isTrue();
        }

        @Test
        @DisplayName("given active provider, when deactivating, then active is false")
        void givenActiveProvider_whenDeactivating_thenActiveIsFalse() {
            // given
            final var provider = TrainingTestFactory.randomExternalProvider();

            // when
            final var deactivated = provider.deactivate();

            // then
            assertThat(deactivated.active()).isFalse();
        }

        @Test
        @DisplayName("given valid attributes, when reconstituting provider, then restore exact state")
        void givenValidAttributes_whenReconstitutingProvider_thenRestoreExactState() {
            // given
            final var id = TrainingTestFactory.randomExternalProviderId();
            final var name = TrainingTestFactory.randomExternalProviderName();
            final var contact = TrainingTestFactory.randomExternalProviderContact();

            // when
            final var reconstituted = ExternalProvider.reconstitute(id, name, contact, false);

            // then
            assertThat(reconstituted.id()).isEqualTo(id);
            assertThat(reconstituted.name()).isEqualTo(name);
            assertThat(reconstituted.contact()).isEqualTo(contact);
            assertThat(reconstituted.active()).isFalse();
        }
    }

    @Nested
    @DisplayName("Entity Identity Semantics")
    class EntityIdentitySemantics {

        @Test
        @DisplayName("given same id with different attributes, when comparing, then they are equal")
        void givenSameIdWithDifferentAttributes_whenComparing_thenTheyAreEqual() {
            // given
            final var id = TrainingTestFactory.randomExternalProviderId();
            final var p1 = ExternalProvider.create(
                    id,
                    TrainingTestFactory.randomExternalProviderName(),
                    TrainingTestFactory.randomExternalProviderContact());
            final var p2 = ExternalProvider.create(
                    id,
                    TrainingTestFactory.randomExternalProviderName(),
                    TrainingTestFactory.randomExternalProviderContact());

            // when

            // then
            assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
        }

        @Test
        @DisplayName("given different ids, when comparing, then they are not equal")
        void givenDifferentIds_whenComparing_thenTheyAreNotEqual() {
            // given
            final var p1 = TrainingTestFactory.randomExternalProvider();
            final var p2 = TrainingTestFactory.randomExternalProvider();

            // when

            // then
            assertThat(p1).isNotEqualTo(p2);
        }
    }
}
