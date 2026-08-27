package com.example.oulearning.organization.application.hierarchy.service;

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
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
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
