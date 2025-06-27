package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressDTO {
    private String name;
    private String tag;
    private String phoneNumber;
    private String location;
}
