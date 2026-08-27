package com.example.oulearning.budgeting.infrastructure.web;

import com.example.oulearning.budgeting.application.exception.BudgetNotFoundException;
import com.example.oulearning.budgeting.domain.exception.BudgetingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = BudgetExceptionHandler.class)
class BudgetExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BudgetNotFoundException.class)
    ProblemDetail handleNotFoundException(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BudgetingException.class)
    ProblemDetail handleDomainException(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
