package com.sep490.gshop.business;

import com.sep490.gshop.entity.ShippingAddress;

import java.util.List;
import java.util.UUID;

public interface ShippingAddressBusiness extends BaseBusiness<ShippingAddress>{
    List<ShippingAddress> findShippingAddressByUserId(UUID userId);
}
