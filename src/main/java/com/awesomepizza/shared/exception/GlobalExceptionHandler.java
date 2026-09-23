package com.awesomepizza.shared.exception;

import com.awesomepizza.shared.dto.ApiErrorResponseDTO;
import com.awesomepizza.shared.dto.FieldValidationErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiErrorResponseDTO> handleBusinessException(
            BusinessException exception, HttpServletRequest request) {
        return buildResponse(exception.getStatus(), exception.getCode(),
                exception.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        List<FieldValidationErrorDTO> violations = exception.getConstraintViolations().stream()
                .map(error -> new FieldValidationErrorDTO(
                        error.getPropertyPath().toString(), error.getMessage()))
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "La richiesta contiene dati non validi", request.getRequestURI(), violations);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponseDTO> handleUnexpectedException(
            Exception exception, HttpServletRequest request) {
        logger.error("Unexpected error while processing the request", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Si è verificato un errore interno", request.getRequestURI(), List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<FieldValidationErrorDTO> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldValidationErrorDTO(error.getField(), error.getDefaultMessage()))
                .toList();
        ApiErrorResponseDTO body = errorBody(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "La richiesta contiene dati non validi", pathOf(request), violations);
        return ResponseEntity.badRequest().body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ApiErrorResponseDTO body = errorBody(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                "Il corpo della richiesta non è leggibile", pathOf(request), List.of());
        return ResponseEntity.badRequest().body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ApiErrorResponseDTO error = errorBody(httpStatus, "REQUEST_ERROR",
                "La richiesta non può essere elaborata", pathOf(request), List.of());
        return ResponseEntity.status(status).headers(headers).body(error);
    }

    private ResponseEntity<ApiErrorResponseDTO> buildResponse(
            HttpStatus status, String code, String message, String path,
            List<FieldValidationErrorDTO> violations) {
        return ResponseEntity.status(status).body(errorBody(status, code, message, path, violations));
    }

    private ApiErrorResponseDTO errorBody(
            HttpStatus status, String code, String message, String path,
            List<FieldValidationErrorDTO> violations) {
        return ApiErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(path)
                .violations(violations)
                .build();
    }

    private String pathOf(WebRequest request) {
        return request instanceof ServletWebRequest servletRequest
                ? servletRequest.getRequest().getRequestURI()
                : "";
    }
}



