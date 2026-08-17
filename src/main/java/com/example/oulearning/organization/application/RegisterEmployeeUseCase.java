package com.example.oulearning.organization.application;

/**
 * Use case to register a new Employee and assign them to an OU.
 */
public interface RegisterEmployeeUseCase {

    String execute(RegisterEmployeeCommand command);
}
