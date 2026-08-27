package com.example.oulearning.organization.application.hierarchy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetSubtreeOrganizationalUnitsServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final GetSubtreeOrganizationalUnitsService service = new GetSubtreeOrganizationalUnitsService(repository);

    @Test
    @DisplayName("given existing subtree, when executing, then return subtree units")
    void givenExistingSubtree_whenExecuting_thenReturnSubtreeUnits() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        when(repository.findSubtreeById(ou.id())).thenReturn(List.of(ou));

        // when
        final var result = service.execute(ou.id());

        // then
        assertThat(result).containsExactly(ou);
    }

    @Test
    @DisplayName("given non-existing unit, when executing, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingUnit_whenExecuting_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        when(repository.findSubtreeById(id)).thenReturn(List.of());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(OrganizationalUnitNotFoundException.class);
    }
}
