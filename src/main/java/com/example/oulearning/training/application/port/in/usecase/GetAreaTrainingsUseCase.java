package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.application.port.in.model.AreaTrainingsOverviewDto;

public interface GetAreaTrainingsUseCase {
    AreaTrainingsOverviewDto execute(OrganizationalUnitId organizationalUnitId);
}
