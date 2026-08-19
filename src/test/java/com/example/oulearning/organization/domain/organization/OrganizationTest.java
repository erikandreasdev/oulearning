package com.example.oulearning.organization.domain.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationException;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import java.time.Instant;
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
        @DisplayName("should create valid Organization snapshot with N-level hierarchy and ouIds set")
        void should_createOrganization_withNLevelHierarchy() {
            final var rootId = DomainGenerators.randomOuId();
            final var engId = DomainGenerators.randomOuId();
            final var feId = DomainGenerators.randomOuId();
            final var beId = DomainGenerators.randomOuId();
            final var salesId = DomainGenerators.randomOuId();

            // Level 3
            final var frontendSub = OrganizationalUnit.leaf(
                    feId,
                    OuName.of("Frontend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    engId);
            final var backendSub = OrganizationalUnit.leaf(
                    beId,
                    OuName.of("Backend"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    engId);

            // Level 2
            final var engineeringArea = OrganizationalUnit.withChildren(
                    engId,
                    OuName.of("Engineering"),
                    OuType.AREA,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    rootId,
                    Set.of(frontendSub, backendSub));

            final var salesSub = OrganizationalUnit.leaf(
                    salesId,
                    OuName.of("Sales"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    rootId);

            // Level 1 Root
            final var rootOu = OrganizationalUnit.withChildren(
                    rootId,
                    OuName.of("Acme Corp"),
                    OuType.ORGANIZATION,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    null, // root has no parent
                    Set.of(engineeringArea, salesSub));

            final var snapshotId = DomainGenerators.randomSnapshotId();
            final var timestamp = Instant.now();

            // when
            final var organization = new Organization(snapshotId, rootOu, timestamp);

            // then
            assertThat(organization.snapshotId()).isEqualTo(snapshotId);
            assertThat(organization.rootOu()).isEqualTo(rootOu);
            assertThat(organization.createdAt()).isEqualTo(timestamp);
            assertThat(organization.ouIds()).containsExactlyInAnyOrder(rootId, engId, feId, beId, salesId);
            assertThat(organization.containsOu(feId)).isTrue();
            assertThat(organization.containsOu(DomainGenerators.randomOuId())).isFalse();
            assertThat(organization.totalOusCount()).isEqualTo(5);
            assertThat(organization.depth()).isEqualTo(3);
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when root OU has parent ID")
        void should_throwException_when_rootOuHasParents() {
            final var invalidRootOu = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Root"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    DomainGenerators.randomOuId()); // Has a parent!

            assertThatThrownBy(() -> new Organization(DomainGenerators.randomSnapshotId(), invalidRootOu, Instant.now()))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("must have no parent ID");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when snapshotId is null")
        void should_throwException_when_snapshotIdIsNull() {
            final var rootOu = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Root"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    null);

            assertThatThrownBy(() -> new Organization(null, rootOu, Instant.now()))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("SnapshotId cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when rootOu is null")
        void should_throwException_when_rootOuIsNull() {
            assertThatThrownBy(() -> new Organization(DomainGenerators.randomSnapshotId(), null, Instant.now()))
                    .isInstanceOf(InvalidOrganizationException.class)
                    .hasMessageContaining("Root OrganizationalUnit cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidOrganizationException when createdAt is null")
        void should_throwException_when_createdAtIsNull() {
            final var rootOu = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("Root"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    null);

            assertThatThrownBy(() -> new Organization(DomainGenerators.randomSnapshotId(), rootOu, null))
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
            final var rootId = DomainGenerators.randomOuId();
            final var subareaId = DomainGenerators.randomOuId();
            final var subarea = OrganizationalUnit.leaf(
                    subareaId,
                    OuName.of("DevOps"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    rootId);

            final var rootOu = OrganizationalUnit.withChildren(
                    rootId,
                    OuName.of("Company"),
                    OuType.ORGANIZATION,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    null,
                    Set.of(subarea));

            final var organization = new Organization(DomainGenerators.randomSnapshotId(), rootOu, Instant.now());

            final var found = organization.findOu(subareaId);
            assertThat(found).contains(subarea);
        }

        @Test
        @DisplayName("should find OU by Name in hierarchy")
        void should_findOuByName() {
            final var rootId = DomainGenerators.randomOuId();
            final var subarea = OrganizationalUnit.leaf(
                    DomainGenerators.randomOuId(),
                    OuName.of("QA Team"),
                    Set.of(DomainGenerators.randomCorporateKey()),
                    rootId);

            final var rootOu = OrganizationalUnit.withChildren(
                    rootId,
                    OuName.of("Company"),
                    OuType.ORGANIZATION,
                    Set.of(DomainGenerators.randomCorporateKey()),
                    null,
                    Set.of(subarea));

            final var organization = new Organization(DomainGenerators.randomSnapshotId(), rootOu, Instant.now());

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
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var snapshotId = DomainGenerators.randomSnapshotId();
            final var rootOu = DomainGenerators.randomOrganization().rootOu();
            final var timestamp = Instant.now();

            final var org1 = new Organization(snapshotId, rootOu, timestamp);
            final var org2 = new Organization(snapshotId, rootOu, timestamp);

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
