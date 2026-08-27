package com.example.oulearning.organization.application.hierarchy.service;
import com.example.oulearning.organization.application.hierarchy.port.in.command.UpdateOrganizationalUnitCommand;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateOrganizationalUnitServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final UpdateOrganizationalUnitService service = new UpdateOrganizationalUnitService(repository);

    @Test
    @DisplayName("given existing OU, when updating name, then renamed OU is saved")
    void givenExistingOu_whenUpdatingName_thenRenamedOuIsSaved() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var newName = HierarchyTestFactory.randomOrganizationalUnitNameString();
        final var command = new UpdateOrganizationalUnitCommand(ou.id(), newName);
        when(repository.findById(ou.id())).thenReturn(Optional.of(ou));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(ou.id());
        assertThat(saved.name().value()).isEqualTo(newName);
    }

    @Test
    @DisplayName("given non-existing OU, when updating name, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingOu_whenUpdatingName_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        final var command = new UpdateOrganizationalUnitCommand(id, HierarchyTestFactory.randomOrganizationalUnitNameString());
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(OrganizationalUnitNotFoundException.class);
    }
}
