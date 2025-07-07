package com.sep490.gshop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageWithTokenResponse {
    private String token;
    private String message;
    private boolean isSuccess;
}
