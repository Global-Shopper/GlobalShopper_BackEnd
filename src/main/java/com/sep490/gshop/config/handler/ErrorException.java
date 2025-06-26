package com.sep490.gshop.config.handler;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorException extends RuntimeException {
    private String message;
    private int httpCode;
    private int errorCode;
}