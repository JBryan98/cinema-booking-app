package com.jbryan98.bookingapp.orchestrator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SagaException.class)
    protected ResponseEntity<Object> handleSagaException(SagaException ex, WebRequest request) {
        log.error("SAGA execution failed: {}", ex.getMessage(), ex);
        var body = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        body.setTitle("SAGA Failed");
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_CONTENT, request);
    }

    @ExceptionHandler(ReserveScreeningFailedByRetriesException.class)
    protected ResponseEntity<Object> handleReserveScreeningFailedByRetriesException(ReserveScreeningFailedByRetriesException ex, WebRequest request) {
        log.error("SAGA execution failed: {}", ex.getMessage(), ex);
        var body = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        body.setTitle("SAGA Failed by Retries in Movie Service -> Reserve Screening");
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(InsufficientSeatsException.class)
    protected ResponseEntity<Object> handleInsufficientSeatsException(InsufficientSeatsException ex, WebRequest request) {
        var body = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        body.setTitle("Insufficient Seats");
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_CONTENT, request);
    }
}
