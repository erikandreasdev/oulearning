package com.example.oulearning.organization.application.hierarchy.service;
import com.example.oulearning.organization.application.hierarchy.port.in.command.RemoveOwnerCommand;

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

import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RemoveOwnerServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final RemoveOwnerService service = new RemoveOwnerService(repository);

    @Test
    @DisplayName("given existing OU with owner, when removing owner, then updated OU without owner is saved")
    void givenExistingOuWithOwner_whenRemovingOwner_thenUpdatedOuWithoutOwnerIsSaved() {
        // given
        final var remainingOwner = EmployeeTestFactory.randomEmployeeId();
        final var ownerToRemove = EmployeeTestFactory.randomEmployeeId();
        final var ou = HierarchyTestFactory.randomOrganizationalUnit().addOwners(Set.of(remainingOwner, ownerToRemove));
        final var command = new RemoveOwnerCommand(ou.id(), Set.of(ownerToRemove));
        when(repository.findById(ou.id())).thenReturn(Optional.of(ou));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.owners()).isNotEmpty().doesNotContain(ownerToRemove);
    }

    @Test
    @DisplayName("given non-existing OU, when removing owners, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingOu_whenRemovingOwners_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        final var command = new RemoveOwnerCommand(id, Set.of(EmployeeTestFactory.randomEmployeeId()));
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(OrganizationalUnitNotFoundException.class);
    }
}
