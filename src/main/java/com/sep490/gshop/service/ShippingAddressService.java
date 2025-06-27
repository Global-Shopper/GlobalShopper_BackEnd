package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import com.sep490.gshop.payload.request.ShippingAddressRequest;

import java.util.List;
import java.util.UUID;

public interface ShippingAddressService {
    ShippingAddressDTO createShippingAddress(ShippingAddressRequest shippingAddressRequest);
    ShippingAddressDTO updateShippingAddress(ShippingAddressRequest shippingAddressRequest, UUID shippingAddressId);
    ShippingAddressDTO getShippingAddress(UUID shippingAddressId);
    List<ShippingAddressDTO> getShippingAddressesByCurrentUser();
    List<ShippingAddressDTO> getShippingAddresses();
    boolean deleteShippingAddress(UUID shippingAddressId);
}
