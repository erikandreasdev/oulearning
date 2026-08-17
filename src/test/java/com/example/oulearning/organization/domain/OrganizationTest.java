package com.example.oulearning.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.shared.domain.Money;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class OrganizationTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create valid Organization snapshot with N-level hierarchy")
        void should_createOrganization_withNLevelHierarchy() {
            // Level 3
            final var frontendSub = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Frontend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(2000.00));
            final var backendSub = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Backend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));

            // Level 2
            final var engineeringArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Engineering"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(5000.00),
                    Set.of(frontendSub, backendSub));

            final var salesSub = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Sales"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(5000.00));

            // Level 1 Root
            final var rootArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Acme Corp"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(), // root has no parents
                    Money.euros(10000.00),
                    Set.of(engineeringArea, salesSub));

            final var snapshotId = DomainGenerators.randomSnapshotId();
            final var timestamp = Instant.now();

            // when
            final var organization = new Organization(snapshotId, rootArea, timestamp);

            // then
            assertThat(organization.snapshotId()).isEqualTo(snapshotId);
            assertThat(organization.rootArea()).isEqualTo(rootArea);
            assertThat(organization.createdAt()).isEqualTo(timestamp);
            assertThat(organization.totalOusCount()).isEqualTo(5);
            assertThat(organization.depth()).isEqualTo(3);
            assertThat(organization.totalBudget()).isEqualTo(Money.euros(10000.00));
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when root Area has parent IDs")
        void should_throwException_when_rootAreaHasParents() {
            final var invalidRootArea = Area.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Root"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(DomainGenerators.randomOuId()), // Has a parent!
                    Money.euros(1000.00),
                    Set.of());

            assertThatThrownBy(() -> new Organization(DomainGenerators.randomSnapshotId(), invalidRootArea, Instant.now()))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("must have no parent IDs");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when snapshotId is null")
        void should_throwException_when_snapshotIdIsNull() {
            final var rootArea = Area.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Root"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(1000.00),
                    Set.of());

            assertThatThrownBy(() -> new Organization(null, rootArea, Instant.now()))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("SnapshotId cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when rootArea is null")
        void should_throwException_when_rootAreaIsNull() {
            assertThatThrownBy(() -> new Organization(DomainGenerators.randomSnapshotId(), null, Instant.now()))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("Root Area cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when createdAt is null")
        void should_throwException_when_createdAtIsNull() {
            final var rootArea = Area.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Root"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(1000.00),
                    Set.of());

            assertThatThrownBy(() -> new Organization(DomainGenerators.randomSnapshotId(), rootArea, null))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("CreatedAt timestamp cannot be null");
        }
    }

    @Nested
    @DisplayName("Query and Search Capabilities")
    class QueryAndSearchCapabilities {

        @Test
        @DisplayName("should find OU by ID in multi-level hierarchy")
        void should_findOuById() {
            final var subareaId = DomainGenerators.randomOuId();
            final var subarea = Subarea.of(
                    subareaId,
                    OuName.of("DevOps"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));

            final var rootArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Company"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00),
                    Set.of(subarea));

            final var organization = new Organization(DomainGenerators.randomSnapshotId(), rootArea, Instant.now());

            final var found = organization.findOu(subareaId);
            assertThat(found).contains(subarea);
        }

        @Test
        @DisplayName("should find OU by Name in hierarchy")
        void should_findOuByName() {
            final var subarea = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("QA Team"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(2500.00));

            final var rootArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("Company"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(2500.00),
                    Set.of(subarea));

            final var organization = new Organization(DomainGenerators.randomSnapshotId(), rootArea, Instant.now());

            final var found = organization.findOu(OuName.of("QA Team"));
            assertThat(found).contains(subarea);
        }

        @Test
        @DisplayName("should return empty when OU is not found")
        void should_returnEmpty_when_ouNotFound() {
            final var organization = DomainGenerators.randomOrganization();

            assertThat(organization.findOu(DomainGenerators.randomOuId())).isEmpty();
            assertThat(organization.findOu(OuName.of("Non Existent"))).isEmpty();
        }
    }

    @Nested
    @DisplayName("Budget Calculations")
    class BudgetCalculations {

        @Test
        @DisplayName("should calculate total budget of a specific list of OUs")
        void should_calculateTotalBudget_ofOUsList() {
            final var ou1 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("OU1"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(1500.00));
            final var ou2 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("OU2"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(2500.00));

            final var organization = DomainGenerators.randomOrganization();
            final var total = organization.totalBudgetOf(List.of(ou1, ou2));

            assertThat(total).isEqualTo(Money.euros(4000.00));
        }

        @Test
        @DisplayName("should calculate subtree budget of a specific Area")
        void should_calculateSubtreeBudget_ofSpecificArea() {
            final var sub1 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Sub1"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));
            final var sub2 = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Sub2"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(4000.00));

            final var engineeringId = DomainGenerators.randomOuId();
            final var engineeringArea = Area.withChildren(
                    engineeringId,
                    OuName.of("Engineering"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(7000.00),
                    Set.of(sub1, sub2));

            final var salesSub = Subarea.of(
                    DomainGenerators.randomOuId(),
                    OuName.of("Sales"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(3000.00));

            final var rootArea = Area.withChildren(
                    DomainGenerators.randomOuId(),
                    OuName.of("HQ"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    Set.of(),
                    Money.euros(10000.00),
                    Set.of(engineeringArea, salesSub));

            final var organization = new Organization(DomainGenerators.randomSnapshotId(), rootArea, Instant.now());

            final var engineeringSubtreeBudget = organization.subtreeBudgetOf(engineeringId);
            assertThat(engineeringSubtreeBudget).isEqualTo(Money.euros(7000.00));

            final var salesSubtreeBudget = organization.subtreeBudgetOf(salesSub.id());
            assertThat(salesSubtreeBudget).isEqualTo(Money.euros(3000.00));
        }

        @Test
        @DisplayName("should throw InvalidOuException when calculating subtree budget for nonexistent OU")
        void should_throwException_when_calculatingSubtreeBudgetForNonexistentOu() {
            final var organization = DomainGenerators.randomOrganization();
            final var nonexistentId = DomainGenerators.randomOuId();

            assertThatThrownBy(() -> organization.subtreeBudgetOf(nonexistentId))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("not found in organization snapshot");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var snapshotId = DomainGenerators.randomSnapshotId();
            final var rootArea = DomainGenerators.randomOrganization().rootArea();
            final var timestamp = Instant.now();

            final var org1 = new Organization(snapshotId, rootArea, timestamp);
            final var org2 = new Organization(snapshotId, rootArea, timestamp);

            assertThat(org1).isEqualTo(org2);
            assertThat(org1.hashCode()).isEqualTo(org2.hashCode());
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var org = DomainGenerators.randomOrganization();
            assertThat(org.getClass().isRecord()).isTrue();
        }
    }
}
