package com.sep490.gshop.external.shipping;

import com.sep490.gshop.entity.ShipmentTrackingEvent;
import com.sep490.gshop.external.shipping.fedex.data.FedexWebhookEvent;
import com.sep490.gshop.payload.request.JSONStringInput;

public interface ShippingTPS {
    String getTrackingToken();
    String getShippingToken();

    String getShippingRate(JSONStringInput inputJson);

    String createShipment(JSONStringInput inputJson);

    String tracking(String trackingNumber);

    ShipmentTrackingEvent webhookToShipmentTrackingEvent(FedexWebhookEvent request, String trackingNumber);
}
