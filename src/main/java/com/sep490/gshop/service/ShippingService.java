package com.sep490.gshop.service;

import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.payload.request.shipment.ShipmentStatusRequest;
import com.sep490.gshop.payload.response.MessageResponse;

public interface ShippingService {
    String getShippingToken(DeliveryCode deliveryCode);

    MessageResponse handleWebhook(ShipmentStatusRequest payload);
}
