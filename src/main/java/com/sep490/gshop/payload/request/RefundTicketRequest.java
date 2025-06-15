package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.RefundStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundTicketRequest {
    @NotBlank(message = "Evidence is required")
    private String evidence;
    @NotBlank(message = "Reason is required")
    private String reason;
    @NotBlank(message = "OrderId is required")
    private String orderId;
}
