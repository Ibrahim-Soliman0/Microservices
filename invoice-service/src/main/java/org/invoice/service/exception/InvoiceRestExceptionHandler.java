package org.invoice.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class InvoiceRestExceptionHandler {

    @ExceptionHandler(exception = {InvoiceNotFoundException.class})
    public ResponseEntity<String> handleException(InvoiceNotFoundException exception) {

        System.out.println("Invoice Not Found:\n" + exception.getMessage());

        return new ResponseEntity<>("Invoice Not Found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        System.out.printf("Unexpected error: %s", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(LocalDateTime.now().toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred"));
    }

    public record ErrorResponse(String timestamp, int status, String message) {}
}