package com.example.oulearning.organization.application.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.IdGenerator;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateOrganizationalUnitServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final CreateOrganizationalUnitService service = new CreateOrganizationalUnitService(repository, idGenerator);

    @Test
    @DisplayName("given valid command, when creating organizational unit, then unit is saved and id is returned")
    void givenValidCommand_whenCreatingOrganizationalUnit_thenUnitIsSavedAndIdReturned() {
        // given
        final var generatedId = HierarchyTestFactory.randomId();
        final var name = HierarchyTestFactory.randomOrganizationalUnitNameString();
        final var parentId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var command = new CreateOrganizationalUnitCommand(name, parentId);
        when(idGenerator.generate()).thenReturn(generatedId);

        // when
        final var resultId = service.execute(command);

        // then
        assertThat(resultId.value()).isEqualTo(generatedId);
        final var captor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id().value()).isEqualTo(generatedId);
        assertThat(saved.name().value()).isEqualTo(name);
        assertThat(saved.parentId()).contains(parentId);
        assertThat(saved.active()).isTrue();
    }
}
