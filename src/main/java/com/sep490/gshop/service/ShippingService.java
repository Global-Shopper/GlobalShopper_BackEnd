package com.sep490.gshop.service;

import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.external.shipping.fedex.data.FedexWebhookEvent;
import com.sep490.gshop.payload.request.JSONStringInput;
import com.sep490.gshop.payload.request.shipment.ShipmentStatusRequest;
import com.sep490.gshop.payload.response.MessageResponse;

public interface ShippingService {
    String getTrackingToken(DeliveryCode deliveryCode);

    MessageResponse handleWebhook(ShipmentStatusRequest payload);

    String getShippingToken(DeliveryCode deliveryCode);

    String getShippingRate(JSONStringInput inputJson);

    String createShipment(JSONStringInput inputJson);

    String tracking(String trackingNumber, DeliveryCode deliveryCode);

    MessageResponse handleFedexWebhook(String trackingNumber, FedexWebhookEvent request);
}
