package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import java.time.Instant;
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
class OrganizationPersistenceAdapterIT {

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
    private OrganizationPersistenceAdapter adapter;

    @Nested
    @DisplayName("Snapshot Persistence & Caching Integration")
    class SnapshotPersistenceAndCachingIntegration {

        @Test
        @DisplayName("should persist complete organization hierarchy and retrieve latest via cache")
        void should_persistOrganization_andRetrieveLatestViaCache() {
            final var sub1 = OrganizationalUnit.leaf(
                    OuId.of(UUID.randomUUID()),
                    OuName.of("Frontend"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());

            final var rootId = OuId.of(UUID.randomUUID());
            final var rootOu = OrganizationalUnit.withChildren(
                    rootId,
                    OuName.of("Global Corp"),
                    OuType.ORGANIZATION,
                    Set.of(CorporateKey.of("CK0099")),
                    Set.of(),
                    Set.of(sub1));

            final var snapshotId = SnapshotId.of(UUID.randomUUID());
            final var organization = new Organization(snapshotId, rootOu, Instant.now());

            adapter.save(organization);

            // Latest snapshot query (cached)
            final var latest = adapter.findLatest();

            assertThat(latest).isPresent();
            assertThat(latest.get().snapshotId()).isEqualTo(snapshotId);
            assertThat(latest.get().rootOu().name().value()).isEqualTo("Global Corp");
            assertThat(latest.get().rootOu().loadedChildren()).hasSize(1);
        }

        @Test
        @DisplayName("should find snapshot by snapshot ID")
        void should_findBySnapshotId() {
            final var rootOu = OrganizationalUnit.leaf(
                    OuId.of(UUID.randomUUID()),
                    OuName.of("Acme"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());

            final var snapshotId = SnapshotId.of(UUID.randomUUID());
            final var organization = new Organization(snapshotId, rootOu, Instant.now());

            adapter.save(organization);

            final var found = adapter.findBySnapshotId(snapshotId);

            assertThat(found).isPresent();
            assertThat(found.get().snapshotId()).isEqualTo(snapshotId);
        }
    }
}
