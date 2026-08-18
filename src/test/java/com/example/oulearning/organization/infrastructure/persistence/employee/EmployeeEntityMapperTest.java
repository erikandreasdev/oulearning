package com.example.oulearning.organization.infrastructure.persistence.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.organization.domain.DomainGenerators;
import com.example.oulearning.organization.domain.employee.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeEntityMapperTest {

    private EmployeeEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EmployeeEntityMapper();
    }

    @Nested
    @DisplayName("Domain to Entity Mapping")
    class DomainToEntityMapping {

        @Test
        @DisplayName("should map full domain Employee to EmployeeEntity correctly")
        void should_mapDomainToEntity_when_fullEmployee() {
            final var employee = DomainGenerators.randomEmployee();

            final var entity = mapper.toEntity(employee, 2L);

            assertThat(entity).isNotNull();
            assertThat(entity.corporateKey()).isEqualTo(employee.corporateKey().value());
            assertThat(entity.firstName()).isEqualTo(employee.fullName().name().value());
            assertThat(entity.lastName()).isEqualTo(employee.fullName().surname().value());
            assertThat(entity.email()).isEqualTo(employee.email().value());
            assertThat(entity.phone()).isEqualTo(employee.phone().value());
            assertThat(entity.role()).isEqualTo(employee.role().name());
            assertThat(entity.ouId()).isEqualTo(employee.ouId().value().toString());
            assertThat(entity.version()).isEqualTo(2L);
        }

        @Test
        @DisplayName("should map null phone domain Employee to EmployeeEntity correctly")
        void should_mapDomainToEntity_when_nullPhone() {
            final var employee = Employee.of(
                    DomainGenerators.randomCorporateKey(),
                    DomainGenerators.randomFullName(),
                    DomainGenerators.randomEmail(),
                    DomainGenerators.randomEmployeeRole(),
                    DomainGenerators.randomOuId());

            final var entity = mapper.toEntity(employee, 0L);

            assertThat(entity).isNotNull();
            assertThat(entity.phone()).isNull();
            assertThat(entity.version()).isEqualTo(0L);
        }

        @Test
        @DisplayName("should return null entity when domain employee is null")
        void should_returnNull_when_domainIsNull() {
            assertThat(mapper.toEntity(null, 0L)).isNull();
        }
    }

    @Nested
    @DisplayName("Entity to Domain Mapping")
    class EntityToDomainMapping {

        @Test
        @DisplayName("should map EmployeeEntity to domain Employee correctly")
        void should_mapEntityToDomain() {
            final var entity = new EmployeeEntity(
                    "CK0001",
                    "Alice",
                    "Smith",
                    "alice@example.com",
                    "+34911223344",
                    "MANAGER",
                    "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                    1L);

            final var domain = mapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.corporateKey().value()).isEqualTo("CK0001");
            assertThat(domain.fullName().name().value()).isEqualTo("Alice");
            assertThat(domain.fullName().surname().value()).isEqualTo("Smith");
            assertThat(domain.email().value()).isEqualTo("alice@example.com");
            assertThat(domain.phone().value()).isEqualTo("+34911223344");
            assertThat(domain.role().name()).isEqualTo("MANAGER");
            assertThat(domain.ouId().value().toString()).isEqualTo("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
        }

        @Test
        @DisplayName("should return null domain when entity is null")
        void should_returnNull_when_entityIsNull() {
            assertThat(mapper.toDomain(null)).isNull();
        }
    }
}
