package com.example.oulearning.organization.domain;

/**
 * Organizational unit type classification.
 */
public enum OuType {
    /**
     * Top-level organization unit (Level 1 root).
     */
    ORGANIZATION,

    /**
     * Intermediate organizational unit (contains child organizational units).
     */
    AREA,

    /**
     * Leaf organizational unit (contains no child organizational units).
     */
    SUBAREA
}
