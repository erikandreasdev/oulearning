package com.example.oulearning.organization.domain;

/**
 * Enumeration of organizational unit types within the organizational hierarchy.
 */
public enum OuType {
    /**
     * Type 1 OU: Represents an Area that contains 0 or more child Subareas.
     */
    AREA,

    /**
     * Type 2 OU: Represents a Subarea that has no child OUs.
     */
    SUBAREA
}
