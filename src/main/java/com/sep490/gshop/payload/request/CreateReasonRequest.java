package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReasonRequest {
    @NotBlank(message = "Vui lòng nhập lý do")
    private String reason;
    @DecimalMin(value = "0.0", inclusive = false, message = "Tỷ lệ hoàn tiền phải lớn hơn 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Tỷ lệ hoàn tiền phải nhỏ hơn hoặc bằng 1")
    private double rate;
}
