package com.sep490.gshop.payload.request.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RePayModel {
    @NotBlank(message = "Cần chọn đơn hàng để thanh toán")
    private String orderId;
    @NotBlank(message = "Cần có liên kết chuyển hướng")
    private String redirectUri;
    @NotBlank(message = "Cần chọn phương thức thanh toán")
    private String paymentMethod;

}
