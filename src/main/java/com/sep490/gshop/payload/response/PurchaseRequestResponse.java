package com.sep490.gshop.payload.response;

import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.dto.PurchaseRequestHistoryDTO;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<PurchaseRequestHistoryDTO> history;
    private T data;
}
