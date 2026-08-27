package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
class MyBatisOrganizationalUnitRepository implements OrganizationalUnitRepository {

    private final OrganizationalUnitMapper mapper;

    MyBatisOrganizationalUnitRepository(final OrganizationalUnitMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<OrganizationalUnit> findById(final OrganizationalUnitId id) {
        return mapper.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void save(final OrganizationalUnit ou) {
        final var entity = toEntity(ou);
        if (mapper.findById(entity.id()).isEmpty()) {
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }

        // Sync owners
        final var existingOwners = mapper.findOwnerIds(entity.id());
        final var newOwners = ou.owners().stream().map(EmployeeId::value).toList();

        existingOwners.stream()
                .filter(ownerId -> !newOwners.contains(ownerId))
                .forEach(ownerId -> mapper.deleteOwner(entity.id(), ownerId));

        newOwners.stream()
                .filter(ownerId -> !existingOwners.contains(ownerId))
                .forEach(ownerId -> mapper.insertOwner(entity.id(), ownerId));

        // Sync members
        final var existingMembers = mapper.findMemberIds(entity.id());
        final var newMembers = ou.members().stream().map(EmployeeId::value).toList();

        existingMembers.stream()
                .filter(memberId -> !newMembers.contains(memberId))
                .forEach(memberId -> mapper.deleteMember(entity.id(), memberId));

        newMembers.stream()
                .filter(memberId -> !existingMembers.contains(memberId))
                .forEach(memberId -> mapper.insertMember(entity.id(), memberId));
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private OrganizationalUnit toDomain(final OrganizationalUnitEntity entity) {
        final var childIds = mapper.findChildIds(entity.id()).stream()
                .map(OrganizationalUnitId::new)
                .collect(Collectors.toSet());

        final var owners = mapper.findOwnerIds(entity.id()).stream()
                .map(EmployeeId::new)
                .collect(Collectors.toSet());

        final var members = mapper.findMemberIds(entity.id()).stream()
                .map(EmployeeId::new)
                .collect(Collectors.toSet());

        final var parentId = entity.parentId() != null ? new OrganizationalUnitId(entity.parentId()) : null;

        return OrganizationalUnit.reconstitute(
                new OrganizationalUnitId(entity.id()),
                new Name(entity.name()),
                parentId,
                childIds,
                owners,
                members,
                entity.active());
    }

    private OrganizationalUnitEntity toEntity(final OrganizationalUnit ou) {
        final Long parentId = ou.parentId().map(OrganizationalUnitId::value).orElse(null);
        return new OrganizationalUnitEntity(
                ou.id().value(),
                ou.name().value(),
                parentId,
                ou.active());
    }
}
