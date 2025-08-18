package com.sep490.gshop.payload.request.refund;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessRefundModel {
    @DecimalMin(value = "0.0", inclusive = false, message = "Tỉ lệ hoàn tiền phải lớn hơn 0")
    @DecimalMax(value = "1.0", message = "Tỉ lệ hoàn tiền phải nhỏ hơn hoặc bằng 1")
    private double refundRate;
}
