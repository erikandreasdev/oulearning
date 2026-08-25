package com.example.oulearning.organization.application.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetOrganizationalUnitServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final GetOrganizationalUnitService service = new GetOrganizationalUnitService(repository);

    @Test
    @DisplayName("given existing OU id, when getting organizational unit, then unit is returned")
    void givenExistingOuId_whenGettingOrganizationalUnit_thenUnitIsReturned() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        when(repository.findById(ou.id())).thenReturn(Optional.of(ou));

        // when
        final var result = service.execute(ou.id());

        // then
        assertThat(result).isEqualTo(ou);
    }

    @Test
    @DisplayName("given non-existing OU id, when getting organizational unit, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingOuId_whenGettingOrganizationalUnit_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(OrganizationalUnitNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
