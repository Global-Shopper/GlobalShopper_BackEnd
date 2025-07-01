package com.sep490.gshop.config.handler;

import com.sep490.gshop.payload.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),new Date(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", HttpStatus.BAD_REQUEST.value());
        body.put("messages", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorMessage> handleAppException(AppException ex) {
        return ResponseEntity.status(ex.getCode())
                .body(new ErrorMessage(ex.getCode(), new Date(), ex.getMessage()));
    }

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorResponse> handleErrorException(ErrorException ex) {
        return ResponseEntity.status(ex.getHttpCode())
                .body(ErrorResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .timestamp(new Date())
                        .build());
    }
}