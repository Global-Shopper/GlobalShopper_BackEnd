package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.entity.ShippingAddress;
import com.sep490.gshop.repository.ShippingAddressRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ShippingAddressBusinessImpl extends BaseBusinessImpl<ShippingAddress, ShippingAddressRepository> implements ShippingAddressBusiness {
    public ShippingAddressBusinessImpl(ShippingAddressRepository repository) {
        super(repository);
    }

    @Override
    public List<ShippingAddress> findShippingAddressByUserId(UUID userId) {
        return repository.findByCustomerId(userId);
    }
}
