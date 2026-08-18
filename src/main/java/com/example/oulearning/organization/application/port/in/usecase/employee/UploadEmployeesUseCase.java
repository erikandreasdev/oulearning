package com.example.oulearning.organization.application.port.in.usecase.employee;

import com.example.oulearning.organization.application.port.in.command.UploadEmployeesCommand;
/**
 * Use case interface for uploading employees from files and linking them to the active organization hierarchy.
 */
public interface UploadEmployeesUseCase {

    int execute(UploadEmployeesCommand command);
}
