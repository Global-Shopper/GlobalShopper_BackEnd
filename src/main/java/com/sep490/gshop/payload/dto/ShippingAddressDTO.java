package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressDTO {
    private UUID id;
    private String name;
    private String tag;
    private String phoneNumber;
    private String location;
    private boolean isDefault;
}
