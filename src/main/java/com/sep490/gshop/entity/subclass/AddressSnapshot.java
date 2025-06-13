package com.sep490.gshop.entity.subclass;

import com.sep490.gshop.entity.ShippingAddress;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressSnapshot {
    private String name;
    private String phoneNumber;
    private String location;
    private boolean isDefault;

    public AddressSnapshot(ShippingAddress shippingAddress) {
        this.name = shippingAddress.getName();
        this.phoneNumber = shippingAddress.getPhoneNumber();
        this.location = shippingAddress.getLocation();
        this.isDefault = shippingAddress.isDefault();
    }
}
