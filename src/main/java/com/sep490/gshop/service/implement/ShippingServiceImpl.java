package com.sep490.gshop.service.implement;

import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.sep490.gshop.external.shipping.ShippingTPSFactory;
import com.sep490.gshop.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingServiceImpl implements ShippingService {

    private final ShippingTPSFactory shippingTPSFactory;

    @Autowired
    public ShippingServiceImpl(ShippingTPSFactory shippingTPSFactory) {
        this.shippingTPSFactory = shippingTPSFactory;
    }


    @Override
    public String getShippingToken(DeliveryCode deliveryCode) {
        ShippingTPS shippingTPS = shippingTPSFactory.getService(deliveryCode);

        return shippingTPS.getShippingToken();
    }
}
