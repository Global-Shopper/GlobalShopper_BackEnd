package com.sep490.gshop.payload.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectCheckoutModel {
    @NotBlank(message = "Cần chọn đơn hàng để thanh toán")
    private String subRequestId;
    @NotNull(message = "Cần có tổng giá trị đơn hàng")
    private double totalPriceEstimate;
    @NotBlank(message = "Cần có liên kết chuyển hướng")
    private String redirectUri;
    private String trackingNumber;
    private Double shippingFee;
}
