package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisOrganizationalUnitRepository.class, FlywayAutoConfiguration.class})
class MyBatisOrganizationalUnitRepositoryIT extends AbstractOracleIntegrationTest {

    @Autowired
    private MyBatisOrganizationalUnitRepository ouRepository;

    @Test
    @DisplayName("given valid OU, when saving, then can be retrieved")
    void givenValidOu_whenSaving_thenCanBeRetrieved() {
        // given
        final var randomName = HierarchyTestFactory.randomOrganizationalUnitNameString();
        final var ou = OrganizationalUnit.create(new OrganizationalUnitId(1L), new Name(randomName), null);

        // when
        ouRepository.save(ou);

        // then
        final var retrieved = ouRepository.findById(new OrganizationalUnitId(1L));

        assertThat(retrieved).isPresent();
        final var ouObj = retrieved.orElseThrow();
        assertThat(ouObj.name().value()).isEqualTo(randomName);
    }

    @Test
    @DisplayName("given existing OU, when updating, then changes are persisted")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-employee.sql", "/sql/insert-ou.sql"})
    void givenExistingOu_whenUpdating_thenChangesArePersisted() {
        // given
        final var ouId = new OrganizationalUnitId(2L);
        var retrieved = ouRepository.findById(ouId).orElseThrow();

        final var randomName = HierarchyTestFactory.randomOrganizationalUnitNameString();
        retrieved = retrieved.addOwner(new EmployeeId(10L));
        retrieved = retrieved.addMember(new EmployeeId(11L));
        retrieved = retrieved.rename(new Name(randomName));

        // when
        ouRepository.save(retrieved);

        // then
        final var updated = ouRepository.findById(ouId);
        assertThat(updated).isPresent();
        final var ouObj = updated.orElseThrow();
        assertThat(ouObj.name().value()).isEqualTo(randomName);
        assertThat(ouObj.owners()).containsExactly(new EmployeeId(10L));
        assertThat(ouObj.members()).containsExactly(new EmployeeId(11L));
    }

    @Test
    @DisplayName("given OU with owners and members, when saving and then removing, then relationships are managed")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-employee.sql", "/sql/insert-ou.sql"})
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
        final var ouObj = updated.orElseThrow();
        assertThat(ouObj.owners()).isEmpty();
        assertThat(ouObj.members()).isEmpty();
    }

    @Test
    @DisplayName("given active OU, when deactivating, then active flag is updated")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-employee.sql", "/sql/insert-ou.sql"})
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
        final var ouObj = updated.orElseThrow();
        assertThat(ouObj.active()).isFalse();
    }
}
