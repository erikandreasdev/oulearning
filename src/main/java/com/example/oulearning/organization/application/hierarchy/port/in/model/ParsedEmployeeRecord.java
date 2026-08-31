package com.example.oulearning.organization.application.hierarchy.port.in.model;

import java.util.List;

public record ParsedEmployeeRecord(
        String name,
        String surname,
        String email,
        boolean isManager,
        List<String> hierarchyPath) {

    public ParsedEmployeeRecord {
        hierarchyPath = hierarchyPath == null ? List.of() : List.copyOf(hierarchyPath);
    }
}
