package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.OuType;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class OrganizationalUnitPersistenceAdapterIT {

    @Container
    static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private OrganizationalUnitPersistenceAdapter adapter;

    @Nested
    @DisplayName("Unit Persistence & Retrieval Integration")
    class UnitPersistenceAndRetrievalIntegration {

        @Test
        @DisplayName("should persist and retrieve leaf unit by ID")
        void should_persistAndFindLeafUnit_byId() {
            final var unitId = OuId.of(UUID.randomUUID());
            final var leaf = OrganizationalUnit.leaf(
                    unitId,
                    OuName.of("Frontend Team"),
                    Set.of(CorporateKey.of("CK0001"), CorporateKey.of("CK0002")),
                    Set.of());

            adapter.save(leaf);

            final var found = adapter.find(OuSearchCriteria.byId(unitId));

            assertThat(found).isPresent();
            assertThat(found.get().id()).isEqualTo(unitId);
            assertThat(found.get().name().value()).isEqualTo("Frontend Team");
            assertThat(found.get().type()).isEqualTo(OuType.SUBAREA);
            assertThat(found.get().owners()).containsExactlyInAnyOrder(CorporateKey.of("CK0001"), CorporateKey.of("CK0002"));
        }

        @Test
        @DisplayName("should persist multi-level hierarchy and load subtree recursively")
        void should_persistAndLoadSubtreeRecursively_when_includeSubtreeIsTrue() {
            final var leaf1 = OrganizationalUnit.leaf(
                    OuId.of(UUID.randomUUID()),
                    OuName.of("Frontend"),
                    Set.of(CorporateKey.of("CK0010")),
                    Set.of());

            final var leaf2 = OrganizationalUnit.leaf(
                    OuId.of(UUID.randomUUID()),
                    OuName.of("Backend"),
                    Set.of(CorporateKey.of("CK0020")),
                    Set.of());

            final var areaId = OuId.of(UUID.randomUUID());
            final var area = OrganizationalUnit.withChildren(
                    areaId,
                    OuName.of("Engineering"),
                    OuType.AREA,
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of(),
                    Set.of(leaf1, leaf2));

            adapter.save(area);

            final var foundWithSubtree = adapter.find(OuSearchCriteria.byId(areaId, true));

            assertThat(foundWithSubtree).isPresent();
            final var retrievedArea = foundWithSubtree.get();
            assertThat(retrievedArea.id()).isEqualTo(areaId);
            assertThat(retrievedArea.loadedChildren()).hasSize(2);
            assertThat(retrievedArea.isSubtreeLoaded()).isTrue();
        }

        @Test
        @DisplayName("should return empty when unit does not exist")
        void should_returnEmpty_when_ouNotFound() {
            final var nonExistentId = OuId.of(UUID.randomUUID());
            final var found = adapter.find(OuSearchCriteria.byId(nonExistentId));

            assertThat(found).isEmpty();
        }
    }
}
