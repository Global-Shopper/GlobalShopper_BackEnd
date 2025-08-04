package com.sep490.gshop.payload.request.refund;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundTicketRequest {
    @NotBlank(message = "Cần có bằng chứng để yêu cầu hoàn tiền")
    private List<String> evidence;

    @NotBlank(message = "Cần nhập lý do hoàn tiền")
    private String reason;
    @NotBlank(message = "Vui lòng chọn đơn hàng để hoàn tiền")
    private String orderId;
}
