package com.sep490.gshop.config.handler;

import lombok.*;

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