package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationImportException;
import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;
import com.example.oulearning.organization.domain.employee.exception.EmployeeException;
import com.example.oulearning.organization.domain.hierarchy.exception.HierarchyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = OrganizationExceptionHandler.class)
class OrganizationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OrganizationalUnitNotFoundException.class)
    ProblemDetail handleNotFoundException(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({EmployeeException.class, HierarchyException.class, OrganizationImportException.class})
    ProblemDetail handleDomainException(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
