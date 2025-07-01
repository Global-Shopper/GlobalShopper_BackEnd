package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequestDTO {
    private String id;
    private ShippingAddressDTO shippingAddress;
    private String status;
    private List<RequestItemDTO> requestItems;
    private AdminDTO admin;
}
