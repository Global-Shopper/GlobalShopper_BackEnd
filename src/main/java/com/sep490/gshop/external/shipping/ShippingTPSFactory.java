package com.sep490.gshop.external.shipping;

import com.sep490.gshop.common.enums.DeliveryCode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ShippingTPSFactory {
    private final Map<String, ShippingTPS> services;

    public ShippingTPSFactory(Map<String, ShippingTPS> services) {
        this.services = services;
    }

    public ShippingTPS getService(DeliveryCode deliveryCode) {
        ShippingTPS service = services.get(deliveryCode.getName().toLowerCase());
        if (service == null) {
            throw new IllegalArgumentException("Unknown shipping provider: " + deliveryCode);
        }
        return service;
    }
}
