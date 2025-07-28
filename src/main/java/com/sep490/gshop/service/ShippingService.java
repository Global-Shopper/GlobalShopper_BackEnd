package com.sep490.gshop.service;

import com.sep490.gshop.common.enums.DeliveryCode;

public interface ShippingService {
    String getShippingToken(DeliveryCode deliveryCode);
}
