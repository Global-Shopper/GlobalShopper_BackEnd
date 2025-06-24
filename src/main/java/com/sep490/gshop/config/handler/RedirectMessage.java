package com.sep490.gshop.config.handler;

import com.sep490.gshop.common.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RedirectMessage {
    private String message;
    private int errorCode;
}
