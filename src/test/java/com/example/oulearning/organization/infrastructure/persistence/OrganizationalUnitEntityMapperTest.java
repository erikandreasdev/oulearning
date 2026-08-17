package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationalUnitEntityMapperTest {

    private final OrganizationalUnitEntityMapper mapper = new OrganizationalUnitEntityMapper();

    @Nested
    @DisplayName("Domain to Entity Mapping")
    class DomainToEntityMapping {

        @Test
        @DisplayName("should map domain OrganizationalUnit to OrganizationalUnitEntity")
        void should_mapDomainToEntity() {
            final var id = OuId.of(UUID.randomUUID());
            final var domain = OrganizationalUnit.leaf(
                    id,
                    OuName.of("Engineering"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());

            final var entity = mapper.toEntity(domain, "snapshot-123", 2L);

            assertThat(entity.id()).isEqualTo(id.toString());
            assertThat(entity.name()).isEqualTo("Engineering");
            assertThat(entity.ouType()).isEqualTo("SUBAREA");
            assertThat(entity.snapshotId()).isEqualTo("snapshot-123");
            assertThat(entity.version()).isEqualTo(2L);
        }

        @Test
        @DisplayName("should default version to 0L when null is passed")
        void should_defaultVersionToZero_when_nullVersion() {
            final var domain = OrganizationalUnit.leaf(
                    OuId.of(UUID.randomUUID()),
                    OuName.of("Sales"),
                    Set.of(CorporateKey.of("CK0002")),
                    Set.of());

            final var entity = mapper.toEntity(domain, null, null);

            assertThat(entity.version()).isEqualTo(0L);
            assertThat(entity.snapshotId()).isNull();
        }

        @Test
        @DisplayName("should throw NullPointerException when domain model is null")
        void should_throwException_when_domainIsNull() {
            assertThatThrownBy(() -> mapper.toEntity(null, null, 0L))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Entity to Domain Mapping")
    class EntityToDomainMapping {

        @Test
        @DisplayName("should map OrganizationalUnitEntity and associations to domain OrganizationalUnit")
        void should_mapEntityToDomain() {
            final var id = UUID.randomUUID();
            final var entity = new OrganizationalUnitEntity(
                    id.toString(), "DevOps", "AREA", "snap-1", 1L);

            final var owners = Set.of(CorporateKey.of("CK0001"));
            final var parentIds = Set.of(OuId.of(UUID.randomUUID()));
            final var childId = OuId.of(UUID.randomUUID());
            final var childUnit = OrganizationalUnit.leaf(childId, OuName.of("Child"), owners, Set.of(OuId.of(id)));

            final var domain = mapper.toDomain(entity, owners, parentIds, Set.of(childId), Set.of(childUnit));

            assertThat(domain.id().value()).isEqualTo(id);
            assertThat(domain.name().value()).isEqualTo("DevOps");
            assertThat(domain.type()).isEqualTo(OuType.AREA);
            assertThat(domain.owners()).isEqualTo(owners);
            assertThat(domain.parentIds()).isEqualTo(parentIds);
            assertThat(domain.childIds()).containsExactly(childId);
            assertThat(domain.loadedChildren()).containsExactly(childUnit);
        }
    }
}
