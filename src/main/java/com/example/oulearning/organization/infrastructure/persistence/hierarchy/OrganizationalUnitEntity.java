package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

public record OrganizationalUnitEntity(Long id, String name, Long parentId, boolean active) {}
