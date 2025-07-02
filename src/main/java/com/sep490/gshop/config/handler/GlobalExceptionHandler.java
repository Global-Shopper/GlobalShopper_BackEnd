package com.sep490.gshop.config.handler;

import com.sep490.gshop.payload.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),new Date(), ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessage> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), "Bạn không có quyền truy cập vào tài nguyên này"));
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