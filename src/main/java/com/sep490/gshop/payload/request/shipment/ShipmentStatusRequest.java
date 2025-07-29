package com.sep490.gshop.payload.request.shipment;

import com.sep490.gshop.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentStatusRequest {
    private String trackingNumber;
    private OrderStatus status;
    private String note;
    private String deliveryCode;
}
