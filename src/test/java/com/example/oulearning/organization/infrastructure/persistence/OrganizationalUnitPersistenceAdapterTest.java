package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.OuType;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationalUnitPersistenceAdapterTest {

    private OrganizationalUnitMyBatisMapper mapper;
    private OrganizationalUnitEntityMapper entityMapper;
    private OrganizationalUnitPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = mock(OrganizationalUnitMyBatisMapper.class);
        entityMapper = new OrganizationalUnitEntityMapper();
        adapter = new OrganizationalUnitPersistenceAdapter(mapper, entityMapper);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("should find unit by ID without loading subtree")
        void should_findUnitById_withoutSubtree() {
            final var unitId = UUID.randomUUID();
            final var unitIdStr = unitId.toString();
            final var parentId = UUID.randomUUID().toString();
            final var childId = UUID.randomUUID().toString();

            final var entity = new OrganizationalUnitEntity(
                    unitIdStr, "Engineering Area", "AREA", null, 0L);

            when(mapper.findUnitById(unitIdStr)).thenReturn(entity);
            when(mapper.findOwnersByOuId(unitIdStr)).thenReturn(Set.of("CK0001"));
            when(mapper.findParentsByOuId(unitIdStr)).thenReturn(Set.of(parentId));
            when(mapper.findChildrenByOuId(unitIdStr)).thenReturn(Set.of(childId));

            final var criteria = OuSearchCriteria.byId(OuId.of(unitId), false);
            final var result = adapter.find(criteria);

            assertThat(result).isPresent();
            final var unit = result.get();
            assertThat(unit.id().value()).isEqualTo(unitId);
            assertThat(unit.name().value()).isEqualTo("Engineering Area");
            assertThat(unit.type()).isEqualTo(OuType.AREA);
            assertThat(unit.owners()).containsExactly(CorporateKey.of("CK0001"));
            assertThat(unit.parentIds()).containsExactly(OuId.of(parentId));
            assertThat(unit.childIds()).containsExactly(OuId.of(childId));
            assertThat(unit.loadedChildren()).isEmpty();
        }

        @Test
        @DisplayName("should find unit by ID and recursively hydrate subtree when includeSubtree is true")
        void should_findUnitById_withSubtree() {
            final var parentId = UUID.randomUUID();
            final var parentIdStr = parentId.toString();
            final var childId = UUID.randomUUID();
            final var childIdStr = childId.toString();

            final var parentEntity = new OrganizationalUnitEntity(
                    parentIdStr, "Parent Unit", "AREA", null, 0L);
            final var childEntity = new OrganizationalUnitEntity(
                    childIdStr, "Child Unit", "SUBAREA", null, 0L);

            when(mapper.findUnitById(parentIdStr)).thenReturn(parentEntity);
            when(mapper.findOwnersByOuId(parentIdStr)).thenReturn(Set.of("CK0001"));
            when(mapper.findParentsByOuId(parentIdStr)).thenReturn(Set.of());
            when(mapper.findChildrenByOuId(parentIdStr)).thenReturn(Set.of(childIdStr));

            when(mapper.findUnitById(childIdStr)).thenReturn(childEntity);
            when(mapper.findOwnersByOuId(childIdStr)).thenReturn(Set.of("CK0002"));
            when(mapper.findParentsByOuId(childIdStr)).thenReturn(Set.of(parentIdStr));
            when(mapper.findChildrenByOuId(childIdStr)).thenReturn(Set.of());

            final var criteria = OuSearchCriteria.byId(OuId.of(parentId), true);
            final var result = adapter.find(criteria);

            assertThat(result).isPresent();
            final var unit = result.get();
            assertThat(unit.loadedChildren()).hasSize(1);
            final var loadedChild = unit.loadedChildren().iterator().next();
            assertThat(loadedChild.id().value()).isEqualTo(childId);
            assertThat(loadedChild.name().value()).isEqualTo("Child Unit");
            assertThat(loadedChild.owners()).containsExactly(CorporateKey.of("CK0002"));
        }

        @Test
        @DisplayName("should find unit by Name")
        void should_findUnitByName() {
            final var unitId = UUID.randomUUID();
            final var unitIdStr = unitId.toString();

            final var entity = new OrganizationalUnitEntity(
                    unitIdStr, "Sales", "SUBAREA", null, 0L);

            when(mapper.findUnitByName("Sales")).thenReturn(entity);
            when(mapper.findOwnersByOuId(unitIdStr)).thenReturn(Set.of("CK1000"));
            when(mapper.findParentsByOuId(unitIdStr)).thenReturn(Set.of());
            when(mapper.findChildrenByOuId(unitIdStr)).thenReturn(Set.of());

            final var result = adapter.find(OuSearchCriteria.byName(OuName.of("Sales")));

            assertThat(result).isPresent();
            assertThat(result.get().id().value()).isEqualTo(unitId);
        }

        @Test
        @DisplayName("should return empty Optional when unit not found")
        void should_returnEmpty_when_unitNotFound() {
            when(mapper.findUnitById(any())).thenReturn(null);

            final var result = adapter.find(OuSearchCriteria.byId(OuId.of(UUID.randomUUID())));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        @DisplayName("should insert new unit when it does not exist")
        void should_insertUnit_when_doesNotExist() {
            final var unitId = OuId.of(UUID.randomUUID());
            final var parentId = OuId.of(UUID.randomUUID());
            final var childId = OuId.of(UUID.randomUUID());
            final var unit = OrganizationalUnit.of(
                    unitId,
                    OuName.of("Engineering"),
                    OuType.AREA,
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of(parentId),
                    Set.of(childId));

            when(mapper.findUnitById(unitId.toString())).thenReturn(null);

            adapter.save(unit);

            verify(mapper).insertUnit(any(OrganizationalUnitEntity.class));
            verify(mapper).insertOwner(unitId.toString(), "CK0001");
            verify(mapper).insertParent(unitId.toString(), parentId.toString());
            verify(mapper).insertChild(unitId.toString(), childId.toString());
        }

        @Test
        @DisplayName("should update existing unit and replace associations")
        void should_updateUnit_when_exists() {
            final var unitId = OuId.of(UUID.randomUUID());
            final var existing = new OrganizationalUnitEntity(
                    unitId.toString(), "Old Name", "AREA", null, 1L);

            final var unit = OrganizationalUnit.of(
                    unitId,
                    OuName.of("New Name"),
                    OuType.AREA,
                    Set.of(CorporateKey.of("CK0002")),
                    Set.of(),
                    Set.of());

            when(mapper.findUnitById(unitId.toString())).thenReturn(existing);

            adapter.save(unit);

            verify(mapper).updateUnit(any(OrganizationalUnitEntity.class));
            verify(mapper).deleteOwnersByOuId(unitId.toString());
            verify(mapper).deleteParentsByOuId(unitId.toString());
            verify(mapper).deleteChildrenByOuId(unitId.toString());
            verify(mapper).insertOwner(unitId.toString(), "CK0002");
        }
    }
}
