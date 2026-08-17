package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

class EmployeePersistenceAdapterTest {

    private EmployeeMyBatisMapper mapper;
    private EmployeeEntityMapper entityMapper;
    private EmployeePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = mock(EmployeeMyBatisMapper.class);
        entityMapper = new EmployeeEntityMapper();
        adapter = new EmployeePersistenceAdapter(mapper, entityMapper);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("should find employee by corporate key")
        void should_findByCorporateKey() {
            final var ouId = UUID.randomUUID().toString();
            final var entity = new EmployeeEntity(
                    "CK0001", "Alice", "Smith", "alice@example.com", "+34911223344", "MANAGER", ouId, 0L);

            when(mapper.findEmployeeByCorporateKey("CK0001")).thenReturn(entity);

            final var result = adapter.findByCorporateKey(CorporateKey.of("CK0001"));

            assertThat(result).isPresent();
            assertThat(result.get().corporateKey().value()).isEqualTo("CK0001");
            assertThat(result.get().fullName().name().value()).isEqualTo("Alice");
            assertThat(result.get().ouId().value().toString()).isEqualTo(ouId);
        }

        @Test
        @DisplayName("should find employees by single OU ID")
        void should_findByOuId() {
            final var ouId = UUID.randomUUID();
            final var e1 = new EmployeeEntity(
                    "CK0001", "Alice", "Smith", "alice@example.com", null, "MANAGER", ouId.toString(), 0L);
            final var e2 = new EmployeeEntity(
                    "CK0002", "Bob", "Jones", "bob@example.com", null, "EMPLOYEE", ouId.toString(), 0L);

            when(mapper.findEmployeesByOuId(ouId.toString())).thenReturn(List.of(e1, e2));

            final var results = adapter.findByOuId(OuId.of(ouId));

            assertThat(results).hasSize(2);
            assertThat(results.get(0).corporateKey().value()).isEqualTo("CK0001");
            assertThat(results.get(1).corporateKey().value()).isEqualTo("CK0002");
        }

        @Test
        @DisplayName("should find employees by collection of OU IDs")
        void should_findByOuIds() {
            final var ou1 = UUID.randomUUID();
            final var ou2 = UUID.randomUUID();
            final var e1 = new EmployeeEntity(
                    "CK0001", "Alice", "Smith", "alice@example.com", null, "MANAGER", ou1.toString(), 0L);
            final var e2 = new EmployeeEntity(
                    "CK0002", "Bob", "Jones", "bob@example.com", null, "EMPLOYEE", ou2.toString(), 0L);

            when(mapper.findEmployeesByOuIds(List.of(ou1.toString(), ou2.toString()))).thenReturn(List.of(e1, e2));

            final var results = adapter.findByOuIds(List.of(OuId.of(ou1), OuId.of(ou2)));

            assertThat(results).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Save and Delete Operations")
    class SaveAndDeleteOperations {

        @Test
        @DisplayName("should insert new employee when not existing")
        void should_insert_whenNotExisting() {
            final var employee = DomainGenerators.randomEmployee();
            when(mapper.findEmployeeByCorporateKey(employee.corporateKey().value())).thenReturn(null);

            adapter.save(employee);

            verify(mapper).insertEmployee(any(EmployeeEntity.class));
        }

        @Test
        @DisplayName("should update employee when already exists")
        void should_update_whenExists() {
            final var employee = DomainGenerators.randomEmployee();
            final var existing = entityMapper.toEntity(employee, 1L);

            when(mapper.findEmployeeByCorporateKey(employee.corporateKey().value())).thenReturn(existing);
            when(mapper.updateEmployee(any(EmployeeEntity.class))).thenReturn(1);

            adapter.save(employee);

            verify(mapper).updateEmployee(any(EmployeeEntity.class));
        }

        @Test
        @DisplayName("should throw OptimisticLockingFailureException when update affected 0 rows")
        void should_throwOptimisticLocking_whenConflict() {
            final var employee = DomainGenerators.randomEmployee();
            final var existing = entityMapper.toEntity(employee, 1L);

            when(mapper.findEmployeeByCorporateKey(employee.corporateKey().value())).thenReturn(existing);
            when(mapper.updateEmployee(any(EmployeeEntity.class))).thenReturn(0);

            assertThatThrownBy(() -> adapter.save(employee))
                    .isInstanceOf(OptimisticLockingFailureException.class);
        }

        @Test
        @DisplayName("should delete employee by corporate key")
        void should_delete() {
            adapter.delete(CorporateKey.of("CK0001"));
            verify(mapper).deleteEmployee("CK0001");
        }
    }
}
