package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationSnapshotEntityMapperTest {

    private final OrganizationSnapshotEntityMapper mapper = new OrganizationSnapshotEntityMapper();

    @Nested
    @DisplayName("Domain to Entity Mapping")
    class DomainToEntityMapping {

        @Test
        @DisplayName("should map domain Organization to OrganizationSnapshotEntity")
        void should_mapDomainToEntity() {
            final var snapshotId = SnapshotId.of(UUID.randomUUID());
            final var rootId = OuId.of(UUID.randomUUID());
            final var rootOu = OrganizationalUnit.leaf(
                    rootId,
                    OuName.of("Root Org"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());
            final var createdAt = Instant.now();
            final var domain = new Organization(snapshotId, rootOu, createdAt);

            final var entity = mapper.toEntity(domain, 3L);

            assertThat(entity.id()).isEqualTo(snapshotId.toString());
            assertThat(entity.rootOuId()).isEqualTo(rootId.toString());
            assertThat(entity.createdAt()).isEqualTo(createdAt);
            assertThat(entity.version()).isEqualTo(3L);
        }

        @Test
        @DisplayName("should throw NullPointerException when domain model is null")
        void should_throwException_when_domainIsNull() {
            assertThatThrownBy(() -> mapper.toEntity(null, 0L))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Entity to Domain Mapping")
    class EntityToDomainMapping {

        @Test
        @DisplayName("should map OrganizationSnapshotEntity and root OU to domain Organization")
        void should_mapEntityToDomain() {
            final var snapshotId = UUID.randomUUID();
            final var rootId = OuId.of(UUID.randomUUID());
            final var createdAt = Instant.now();

            final var entity = new OrganizationSnapshotEntity(
                    snapshotId.toString(), rootId.toString(), createdAt, 1L);

            final var rootOu = OrganizationalUnit.leaf(
                    rootId,
                    OuName.of("Root Org"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());

            final var domain = mapper.toDomain(entity, rootOu);

            assertThat(domain.snapshotId().value()).isEqualTo(snapshotId);
            assertThat(domain.rootOu()).isEqualTo(rootOu);
            assertThat(domain.createdAt()).isEqualTo(createdAt);
        }
    }
}
