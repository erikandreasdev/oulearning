package com.example.oulearning.organization.application.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DeleteOrganizationalUnitServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final DeleteOrganizationalUnitService service = new DeleteOrganizationalUnitService(repository);

    @Test
    @DisplayName("given existing OU, when deleting, then OU is deactivated and saved")
    void givenExistingOu_whenDeleting_thenOuIsDeactivatedAndSaved() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        when(repository.findById(ou.id())).thenReturn(Optional.of(ou));

        // when
        service.execute(ou.id());

        // then
        final var captor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(ou.id());
        assertThat(saved.active()).isFalse();
    }

    @Test
    @DisplayName("given non-existing OU, when deleting, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingOu_whenDeleting_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(OrganizationalUnitNotFoundException.class);
    }
}
