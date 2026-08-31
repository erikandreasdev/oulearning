package com.example.oulearning.organization.application.hierarchy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ListOrganizationalUnitsServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final ListOrganizationalUnitsService service = new ListOrganizationalUnitsService(repository);

    @Test
    @DisplayName("given existing organizational units, when listing all units, then returns all units from repository")
    void givenExistingOrganizationalUnits_whenListingAllUnits_thenReturnsAllUnitsFromRepository() {
        // given
        final var unit1 = OrganizationalUnit.of(new OrganizationalUnitId(1L), Name.of("Global"));
        final var unit2 = OrganizationalUnit.of(new OrganizationalUnitId(2L), Name.of("Corporate"), new OrganizationalUnitId(1L), Set.of(), Set.of(), Set.of());
        when(repository.findAll()).thenReturn(List.of(unit1, unit2));

        // when
        final var result = service.execute();

        // then
        assertThat(result).containsExactly(unit1, unit2);
    }
}
