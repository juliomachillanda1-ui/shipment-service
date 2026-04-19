package com.shipment.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ShipmentNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(404).body(
                new ErrorResponse(LocalDateTime.now(), 404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleConflict(InvalidStateTransitionException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(
                new ErrorResponse(LocalDateTime.now(), 409, "Conflict", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessable(BusinessRuleException ex, HttpServletRequest request) {
        return ResponseEntity.status(422).body(
                new ErrorResponse(LocalDateTime.now(), 422, "Unprocessable Entity", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(400).body(
                new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", "Validación fallida", request.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(500).body(
                new ErrorResponse(LocalDateTime.now(), 500, "Internal Server Error", ex.getMessage(), request.getRequestURI()));
    }
}
