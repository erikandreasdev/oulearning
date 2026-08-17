package com.example.oulearning.organization.application;

/**
 * Use case interface for uploading employees from files and linking them to the active organization hierarchy.
 */
public interface UploadEmployeesUseCase {

    int execute(UploadEmployeesCommand command);
}
