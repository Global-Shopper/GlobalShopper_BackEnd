package com.sep490.gshop.payload.request.bm;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceFeeConfigModel {
    @NotNull(message = "Tỉ lệ phí dịch vụ không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tỉ lệ phí dịch vụ phải lớn hơn 0")
    @DecimalMax(value = "1.0", message = "Tỉ lệ phí dịch vụ phải nhỏ hơn hoặc bằng 1")
    private Double serviceFee;
}
