package com.example.oulearning.organization.domain.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.unit.exception.InvalidOuException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class OuSearchCriteriaTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create search criteria by ID with subtree flag")
        void should_createCriteria_byId_withSubtreeFlag() {
            final var id = DomainGenerators.randomOuId();
            final var criteria = OuSearchCriteria.byId(id, true);

            assertThat(criteria.id()).isEqualTo(id);
            assertThat(criteria.findId()).contains(id);
            assertThat(criteria.name()).isNull();
            assertThat(criteria.findName()).isEmpty();
            assertThat(criteria.includeSubtree()).isTrue();
        }

        @Test
        @DisplayName("should create search criteria by ID with default subtree false")
        void should_createCriteria_byId_defaultSubtreeFalse() {
            final var id = DomainGenerators.randomOuId();
            final var criteria = OuSearchCriteria.byId(id);

            assertThat(criteria.id()).isEqualTo(id);
            assertThat(criteria.includeSubtree()).isFalse();
        }

        @Test
        @DisplayName("should create search criteria by Name with subtree flag")
        void should_createCriteria_byName_withSubtreeFlag() {
            final var name = DomainGenerators.randomOuName();
            final var criteria = OuSearchCriteria.byName(name, true);

            assertThat(criteria.name()).isEqualTo(name);
            assertThat(criteria.findName()).contains(name);
            assertThat(criteria.id()).isNull();
            assertThat(criteria.findId()).isEmpty();
            assertThat(criteria.includeSubtree()).isTrue();
        }

        @Test
        @DisplayName("should create search criteria by Name with default subtree false")
        void should_createCriteria_byName_defaultSubtreeFalse() {
            final var name = DomainGenerators.randomOuName();
            final var criteria = OuSearchCriteria.byName(name);

            assertThat(criteria.name()).isEqualTo(name);
            assertThat(criteria.includeSubtree()).isFalse();
        }

        @Test
        @DisplayName("should create search criteria with both ID and Name")
        void should_createCriteria_withBothIdAndName() {
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var criteria = OuSearchCriteria.of(id, name, true);

            assertThat(criteria.findId()).contains(id);
            assertThat(criteria.findName()).contains(name);
            assertThat(criteria.includeSubtree()).isTrue();
        }

        @Test
        @DisplayName("should throw InvalidOuException when both ID and Name are null")
        void should_throwException_when_bothIdAndNameAreNull() {
            assertThatThrownBy(() -> new OuSearchCriteria(null, null, false))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("At least one search key");
        }

        @Test
        @DisplayName("should throw InvalidOuException when null ID passed to byId factory")
        void should_throwException_when_nullIdPassedToById() {
            assertThatThrownBy(() -> OuSearchCriteria.byId(null, false))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("OuId cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when null Name passed to byName factory")
        void should_throwException_when_nullNamePassedToByName() {
            assertThatThrownBy(() -> OuSearchCriteria.byName(null, false))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("OuName cannot be null");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();

            final var c1 = OuSearchCriteria.of(id, name, true);
            final var c2 = OuSearchCriteria.of(id, name, true);

            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when includeSubtree differs")
        void should_notBeEqual_when_subtreeDiffers() {
            final var id = DomainGenerators.randomOuId();

            final var c1 = OuSearchCriteria.byId(id, true);
            final var c2 = OuSearchCriteria.byId(id, false);

            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var criteria = OuSearchCriteria.byId(DomainGenerators.randomOuId());
            assertThat(criteria.getClass().isRecord()).isTrue();
        }
    }
}
