package com.sep490.gshop.payload.request.order;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInformationModel {
    @NotBlank(message = "Phải có đơn vị vận chuyển")
    private String name;
    private String orderCode;
    @NotBlank(message = "Phải có mã vận chuyển")
    private String trackingNumber;
}
