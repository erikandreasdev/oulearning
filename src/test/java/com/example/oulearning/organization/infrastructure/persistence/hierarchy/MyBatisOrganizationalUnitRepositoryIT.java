package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisOrganizationalUnitRepository.class, FlywayAutoConfiguration.class})
@Testcontainers
class MyBatisOrganizationalUnitRepositoryIT {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim");

    @Autowired
    private MyBatisOrganizationalUnitRepository ouRepository;

    @Test
    @DisplayName("given valid OU, when saving, then can be retrieved")
    void givenValidOu_whenSaving_thenCanBeRetrieved() {
        // given
        final var ou = OrganizationalUnit.create(new OrganizationalUnitId(1L), new Name("Engineering"), null);

        // when
        ouRepository.save(ou);

        // then
        // Assuming ID is 1 for the first insert
        final var retrieved = ouRepository.findById(new OrganizationalUnitId(1L));

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().name().value()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("given existing OU, when updating, then changes are persisted")
    @Sql(scripts = "/sql/insert-ou.sql")
    void givenExistingOu_whenUpdating_thenChangesArePersisted() {
        // given
        final var ouId = new OrganizationalUnitId(2L);
        var retrieved = ouRepository.findById(ouId).orElseThrow();

        retrieved = retrieved.addOwner(new EmployeeId(10L));
        retrieved = retrieved.addMember(new EmployeeId(20L));
        retrieved.rename(new Name("Global Sales"));

        // when
        ouRepository.save(retrieved);

        // then
        final var updated = ouRepository.findById(ouId);
        assertThat(updated).isPresent();
        assertThat(updated.get().name().value()).isEqualTo("Global Sales");
        assertThat(updated.get().owners()).containsExactly(new EmployeeId(10L));
        assertThat(updated.get().members()).containsExactly(new EmployeeId(20L));
    }

    @Test
    @DisplayName("given OU with owners and members, when saving and then removing, then relationships are managed")
    @Sql(scripts = "/sql/insert-ou.sql")
    void givenOUWithOwnersAndMembers_whenSavingAndThenRemoving_thenRelationshipsAreManaged() {
        // given
        final var ouId = new OrganizationalUnitId(2L);
        var retrieved = ouRepository.findById(ouId).orElseThrow();
        retrieved = retrieved.addOwner(new EmployeeId(10L));
        retrieved = retrieved.addMember(new EmployeeId(11L));
        ouRepository.save(retrieved);

        retrieved = ouRepository.findById(ouId).orElseThrow();
        assertThat(retrieved.owners()).containsExactly(new EmployeeId(10L));
        assertThat(retrieved.members()).containsExactly(new EmployeeId(11L));

        retrieved = retrieved.removeOwner(new EmployeeId(10L));
        retrieved = retrieved.removeMember(new EmployeeId(11L));

        // when
        ouRepository.save(retrieved);

        // then
        final var updated = ouRepository.findById(ouId);
        assertThat(updated).isPresent();
        assertThat(updated.get().owners()).isEmpty();
        assertThat(updated.get().members()).isEmpty();
    }

    @Test
    @DisplayName("given active OU, when deactivating, then active flag is updated")
    @Sql(scripts = "/sql/insert-ou.sql")
    void givenActiveOU_whenDeactivating_thenActiveFlagIsUpdated() {
        // given
        final var ouId = new OrganizationalUnitId(2L);
        final var retrieved = ouRepository.findById(ouId).orElseThrow();
        final var deactivatedOu = retrieved.deactivate();

        // when
        ouRepository.save(deactivatedOu);

        // then
        final var updated = ouRepository.findById(ouId);
        assertThat(updated).isPresent();
        assertThat(updated.get().active()).isFalse();
    }
}
