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

class RemoveMemberServiceTest {

    private final OrganizationalUnitRepository repository = mock(OrganizationalUnitRepository.class);
    private final RemoveMemberService service = new RemoveMemberService(repository);

    @Test
    @DisplayName("given existing OU with member, when removing member, then updated OU without member is saved")
    void givenExistingOuWithMember_whenRemovingMember_thenUpdatedOuWithoutMemberIsSaved() {
        // given
        final var remainingMember = EmployeeTestFactory.randomEmployeeId();
        final var memberToRemove = EmployeeTestFactory.randomEmployeeId();
        final var ou = HierarchyTestFactory.randomOrganizationalUnit().addMembers(Set.of(remainingMember, memberToRemove));
        final var command = new RemoveMemberCommand(ou.id(), Set.of(memberToRemove));
        when(repository.findById(ou.id())).thenReturn(Optional.of(ou));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.members()).isNotEmpty().doesNotContain(memberToRemove);
    }

    @Test
    @DisplayName("given non-existing OU, when removing members, then throw OrganizationalUnitNotFoundException")
    void givenNonExistingOu_whenRemovingMembers_thenThrowOrganizationalUnitNotFoundException() {
        // given
        final var id = HierarchyTestFactory.randomOrganizationalUnitId();
        final var command = new RemoveMemberCommand(id, Set.of(EmployeeTestFactory.randomEmployeeId()));
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(OrganizationalUnitNotFoundException.class);
    }
}
