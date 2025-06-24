package com.sep490.gshop.config.handler;

import com.sep490.gshop.common.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedirectException extends RuntimeException {
    private String message;
    private int httpCode;
    private int errorCode;
}
