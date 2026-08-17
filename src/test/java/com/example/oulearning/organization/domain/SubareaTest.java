package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;
import java.util.Set;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class SubareaTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create Subarea with valid parameters")
        void should_createSubarea_when_validParametersProvided() {
            // given
            final var id = DomainGenerators.randomOuId();
            final var name = DomainGenerators.randomOuName();
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(5000.00);

            // when
            final var subarea = Subarea.of(id, name, owners, parentIds, budget);

            // then
            assertThat(subarea.id()).isEqualTo(id);
            assertThat(subarea.name()).isEqualTo(name);
            assertThat(subarea.owners()).isEqualTo(owners);
            assertThat(subarea.parentIds()).isEqualTo(parentIds);
            assertThat(subarea.budget()).isEqualTo(budget);
            assertThat(subarea.type()).isEqualTo(OuType.SUBAREA);
            assertThat(subarea.childIds()).isEmpty();
            assertThat(subarea.totalSubtreeBudget()).isEqualTo(budget);
        }

        @Test
        @DisplayName("should create Subarea with empty parents (0 parents)")
        void should_createSubarea_with_emptyParents() {
            final var subarea = Subarea.of(
                    DomainGenerators.randomOuId(),
                    DomainGenerators.randomOuName(),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(1000.00));

            assertThat(subarea.parentIds()).isEmpty();
        }

        @Test
        @DisplayName("should throw InvalidOuException when ID is null")
        void should_throwException_when_idIsNull() {
            assertThatThrownBy(() -> Subarea.of(
                            null,
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            Money.euros(1000.00)))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("ID cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when name is null")
        void should_throwException_when_nameIsNull() {
            assertThatThrownBy(() -> Subarea.of(
                            DomainGenerators.randomOuId(),
                            null,
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            Money.euros(1000.00)))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("name cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOuException when budget is null")
        void should_throwException_when_budgetIsNull() {
            assertThatThrownBy(() -> Subarea.of(
                            DomainGenerators.randomOuId(),
                            DomainGenerators.randomOuName(),
                            Set.of(DomainGenerators.randomCorporateKey()),
                            Set.of(),
                            null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("budget cannot be null");
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
            final var owners = Set.of(DomainGenerators.randomCorporateKey());
            final var parentIds = Set.of(DomainGenerators.randomOuId());
            final var budget = Money.euros(2500.00);

            final var sub1 = Subarea.of(id, name, owners, parentIds, budget);
            final var sub2 = Subarea.of(id, name, owners, parentIds, budget);

            assertThat(sub1).isEqualTo(sub2);
            assertThat(sub1.hashCode()).isEqualTo(sub2.hashCode());
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var subarea = DomainGenerators.randomSubarea();
            assertThat(subarea.getClass().isRecord()).isTrue();
        }
    }
}
