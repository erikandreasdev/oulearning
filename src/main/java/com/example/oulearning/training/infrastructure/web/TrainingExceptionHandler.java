package com.example.oulearning.training.infrastructure.web;

import com.example.oulearning.training.application.exception.TrainingNotFoundException;
import com.example.oulearning.training.domain.exception.TrainingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = TrainingExceptionHandler.class)
class TrainingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TrainingNotFoundException.class)
    ProblemDetail handleNotFoundException(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TrainingException.class)
    ProblemDetail handleDomainException(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
