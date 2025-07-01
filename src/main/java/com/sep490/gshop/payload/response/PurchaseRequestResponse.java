package com.sep490.gshop.payload.response;

import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestResponse<T> {
    private String id;
    private ShippingAddressDTO shippingAddress;
    private String status;
    private AdminDTO admin;
    private String requestType;
    private T data;
}
