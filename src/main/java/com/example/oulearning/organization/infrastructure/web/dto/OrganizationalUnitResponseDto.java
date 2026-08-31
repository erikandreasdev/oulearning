package com.example.oulearning.organization.infrastructure.web.dto;

import java.util.Set;

public record OrganizationalUnitResponseDto(
        Long id,
        String name,
        Long parentId,
        Set<Long> childIds,
        Set<Long> owners,
        Set<Long> members,
        boolean active) {

    public OrganizationalUnitResponseDto {
        childIds = childIds == null ? Set.of() : Set.copyOf(childIds);
        owners = owners == null ? Set.of() : Set.copyOf(owners);
        members = members == null ? Set.of() : Set.copyOf(members);
    }
}
