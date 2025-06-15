package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.entity.ShippingAddress;
import com.sep490.gshop.repository.ShippingAddressRepository;

public class ShippingAddressBusinessImpl extends BaseBusinessImpl<ShippingAddress, ShippingAddressRepository> implements ShippingAddressBusiness {
    public ShippingAddressBusinessImpl(ShippingAddressRepository repository) {
        super(repository);
    }
}
