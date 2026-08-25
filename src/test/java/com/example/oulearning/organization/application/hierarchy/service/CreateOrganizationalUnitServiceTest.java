package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.IdGenerator;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
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
