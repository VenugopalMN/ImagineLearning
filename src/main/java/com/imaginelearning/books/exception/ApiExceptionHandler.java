package com.imaginelearning.books.exception;

import com.imaginelearning.books.dto.error.ApiError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            BadRequestException.class,
            MissingServletRequestParameterException.class,
            HandlerMethodValidationException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
        String message = extractValidationMessage(exception);
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    @ExceptionHandler(ExternalServiceBadResponseException.class)
    public ResponseEntity<ApiError> handleExternalServiceBadResponse(ExternalServiceBadResponseException exception) {
        return buildError(HttpStatus.BAD_GATEWAY, "BAD_GATEWAY", exception.getMessage());
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleExternalServiceUnavailable(ExternalServiceUnavailableException exception) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.");
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(code, message, Instant.now()));
    }

    private String extractValidationMessage(Exception exception) {
        if (exception instanceof BadRequestException badRequestException) {
            return badRequestException.getMessage();
        }

        if (exception instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
            return missingServletRequestParameterException.getParameterName() + " must be present and not blank";
        }

        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException
                && methodArgumentNotValidException.getBindingResult().hasFieldErrors()) {
            return methodArgumentNotValidException.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        }

        if (exception instanceof HandlerMethodValidationException handlerMethodValidationException) {
            return handlerMethodValidationException.getAllValidationResults().stream()
                    .flatMap(result -> result.getResolvableErrors().stream())
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .filter(defaultMessage -> defaultMessage != null && !defaultMessage.isBlank())
                    .findFirst()
                    .orElse("Invalid request.");
        }

        if (exception instanceof ConstraintViolationException constraintViolationException
                && !constraintViolationException.getConstraintViolations().isEmpty()) {
            return constraintViolationException.getConstraintViolations().iterator().next().getMessage();
        }

        return "Invalid request.";
    }
}
