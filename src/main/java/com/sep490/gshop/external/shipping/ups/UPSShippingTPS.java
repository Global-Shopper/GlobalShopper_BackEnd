package com.sep490.gshop.external.shipping.ups;

import com.sep490.gshop.entity.ShipmentTrackingEvent;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.sep490.gshop.external.shipping.fedex.data.FedexWebhookEvent;
import com.sep490.gshop.payload.request.JSONStringInput;
import org.springframework.stereotype.Service;

@Service("ups")
public class UPSShippingTPS implements ShippingTPS {
    @Override
    public String getTrackingToken() {
        return "";
    }

    @Override
    public String getShippingToken() {
        return "ups-token-12345";
    }

    @Override
    public String getShippingRate(JSONStringInput inputJson) {
        return "";
    }

    @Override
    public String createShipment(JSONStringInput inputJson) {
        return "";
    }

    @Override
    public String tracking(String trackingNumber) {
        return "";
    }

    @Override
    public ShipmentTrackingEvent webhookToShipmentTrackingEvent(FedexWebhookEvent request, String trackingNumber) {
        return null;
    }
}
