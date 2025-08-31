package com.sep490.gshop.payload.request.refund;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
    @DecimalMin(value = "0.0", inclusive = false, message = "Tỷ lệ hoàn tiền phải lớn hơn 0")
    @DecimalMax(value = "1.0", message = "Tỷ lệ hoàn tiền phải nhỏ hơn hoặc bằng 1")
    private Double rate;
}
