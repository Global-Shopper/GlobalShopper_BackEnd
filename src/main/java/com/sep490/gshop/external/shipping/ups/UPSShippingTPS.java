package com.sep490.gshop.external.shipping.ups;

import com.sep490.gshop.external.shipping.ShippingTPS;
import org.springframework.stereotype.Service;

@Service("ups")
public class UPSShippingTPS implements ShippingTPS {
    @Override
    public String getShippingToken() {
        return "ups-token-12345";
    }
}
