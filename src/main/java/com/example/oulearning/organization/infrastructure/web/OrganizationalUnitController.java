package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.application.hierarchy.port.in.AssignMemberCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.AssignMemberUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.AssignOwnerCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.AssignOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.CreateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.CreateOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.DeleteOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.RemoveMemberCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.RemoveMemberUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.RemoveOwnerCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.RemoveOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.UpdateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.UpdateOrganizationalUnitUseCase;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.infrastructure.web.api.OrganizationalUnitsApi;
import com.example.oulearning.organization.infrastructure.web.dto.AssignEmployeeRequest;
import com.example.oulearning.organization.infrastructure.web.dto.CreateOrganizationalUnitRequest;
import com.example.oulearning.organization.infrastructure.web.dto.OrganizationalUnitResponse;
import com.example.oulearning.organization.infrastructure.web.dto.UpdateOrganizationalUnitRequest;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OrganizationalUnitController implements OrganizationalUnitsApi {

    private final CreateOrganizationalUnitUseCase createOrganizationalUnitUseCase;
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase;
    private final UpdateOrganizationalUnitUseCase updateOrganizationalUnitUseCase;
    private final DeleteOrganizationalUnitUseCase deleteOrganizationalUnitUseCase;
    private final AssignMemberUseCase assignMemberUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;
    private final AssignOwnerUseCase assignOwnerUseCase;
    private final RemoveOwnerUseCase removeOwnerUseCase;

    OrganizationalUnitController(
            final CreateOrganizationalUnitUseCase createOrganizationalUnitUseCase,
            final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase,
            final UpdateOrganizationalUnitUseCase updateOrganizationalUnitUseCase,
            final DeleteOrganizationalUnitUseCase deleteOrganizationalUnitUseCase,
            final AssignMemberUseCase assignMemberUseCase,
            final RemoveMemberUseCase removeMemberUseCase,
            final AssignOwnerUseCase assignOwnerUseCase,
            final RemoveOwnerUseCase removeOwnerUseCase) {
        this.createOrganizationalUnitUseCase = createOrganizationalUnitUseCase;
        this.getOrganizationalUnitUseCase = getOrganizationalUnitUseCase;
        this.updateOrganizationalUnitUseCase = updateOrganizationalUnitUseCase;
        this.deleteOrganizationalUnitUseCase = deleteOrganizationalUnitUseCase;
        this.assignMemberUseCase = assignMemberUseCase;
        this.removeMemberUseCase = removeMemberUseCase;
        this.assignOwnerUseCase = assignOwnerUseCase;
        this.removeOwnerUseCase = removeOwnerUseCase;
    }

    @Override
    public ResponseEntity<OrganizationalUnitResponse> createOrganizationalUnit(final CreateOrganizationalUnitRequest request) {
        final var parentId = request.getParentId() != null ? new OrganizationalUnitId(request.getParentId()) : null;
        final var command = new CreateOrganizationalUnitCommand(request.getName(), parentId);
        final var id = createOrganizationalUnitUseCase.execute(command);
        final var ou = getOrganizationalUnitUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(ou));
    }

    @Override
    public ResponseEntity<OrganizationalUnitResponse> getOrganizationalUnit(final Long id) {
        final var ou = getOrganizationalUnitUseCase.execute(new OrganizationalUnitId(id));
        return ResponseEntity.ok(toResponse(ou));
    }

    @Override
    public ResponseEntity<OrganizationalUnitResponse> updateOrganizationalUnit(final Long id, final UpdateOrganizationalUnitRequest request) {
        final var command = new UpdateOrganizationalUnitCommand(new OrganizationalUnitId(id), request.getName());
        updateOrganizationalUnitUseCase.execute(command);
        final var ou = getOrganizationalUnitUseCase.execute(new OrganizationalUnitId(id));
        return ResponseEntity.ok(toResponse(ou));
    }

    @Override
    public ResponseEntity<Void> deleteOrganizationalUnit(final Long id) {
        deleteOrganizationalUnitUseCase.execute(new OrganizationalUnitId(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> assignMember(final Long id, final AssignEmployeeRequest request) {
        final var command = new AssignMemberCommand(new OrganizationalUnitId(id), Set.of(new EmployeeId(request.getEmployeeId())));
        assignMemberUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeMember(final Long id, final Long employeeId) {
        final var command = new RemoveMemberCommand(new OrganizationalUnitId(id), Set.of(new EmployeeId(employeeId)));
        removeMemberUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> assignOwner(final Long id, final AssignEmployeeRequest request) {
        final var command = new AssignOwnerCommand(new OrganizationalUnitId(id), Set.of(new EmployeeId(request.getEmployeeId())));
        assignOwnerUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> removeOwner(final Long id, final Long employeeId) {
        final var command = new RemoveOwnerCommand(new OrganizationalUnitId(id), Set.of(new EmployeeId(employeeId)));
        removeOwnerUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    private OrganizationalUnitResponse toResponse(final OrganizationalUnit ou) {
        final var response = new OrganizationalUnitResponse();
        response.setId(ou.id().value());
        response.setName(ou.name().value());
        response.setParentId(ou.parentId().map(OrganizationalUnitId::value).orElse(null));
        response.setActive(ou.active());

        final List<Long> childIds = ou.childIds().stream().map(OrganizationalUnitId::value).toList();
        response.setChildIds(childIds);

        final List<Long> owners = ou.owners().stream().map(EmployeeId::value).toList();
        response.setOwners(owners);

        final List<Long> members = ou.members().stream().map(EmployeeId::value).toList();
        response.setMembers(members);

        return response;
    }
}
