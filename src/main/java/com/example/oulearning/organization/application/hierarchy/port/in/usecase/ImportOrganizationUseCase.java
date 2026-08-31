package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.model.ImportOrganizationResult;
import java.io.InputStream;

public interface ImportOrganizationUseCase {
    ImportOrganizationResult execute(InputStream inputStream);
}
