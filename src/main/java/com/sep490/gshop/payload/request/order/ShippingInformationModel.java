package com.sep490.gshop.payload.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInformationModel {
    private String name;
    private String orderCode;
    private String trackingNumber;
}
