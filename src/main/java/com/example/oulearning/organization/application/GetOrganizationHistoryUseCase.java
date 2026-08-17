package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.organization.Organization;
import java.util.List;

/**
 * Use case input port for retrieving full chronological history of organization snapshots.
 */
public interface GetOrganizationHistoryUseCase {
    List<Organization> execute();
}
