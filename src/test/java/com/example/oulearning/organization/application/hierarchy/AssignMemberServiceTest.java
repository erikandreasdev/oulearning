package com.example.oulearning.organization.application.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AssignMemberServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final AssignMemberService service = new AssignMemberService(repository);

    @Test
    @DisplayName("given existing OU, when assigning members, then updated OU with members is saved")
    void givenExistingOu_whenAssigningMembers_thenUpdatedOuWithMembersIsSaved() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var newMember1 = EmployeeTestFactory.randomEmployeeId();
        final var newMember2 = EmployeeTestFactory.randomEmployeeId();
        final var command = new AssignMemberCommand(ou.id(), Set.of(newMember1, newMember2));
        when(repository.findById(ou.id())).thenReturn(Optional.of(ou));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.members()).contains(newMember1, newMember2);
    }

    @Test
    @DisplayName("given non-existing OU, when assigning members, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingOu_whenAssigningMembers_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        final var command = new AssignMemberCommand(id, Set.of(EmployeeTestFactory.randomEmployeeId()));
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(OrganizationalUnitNotFoundException.class);
    }
}
