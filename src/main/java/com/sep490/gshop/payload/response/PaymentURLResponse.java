package com.sep490.gshop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentURLResponse {
    private String message;
    private String url;
    private boolean isSuccess;
}
