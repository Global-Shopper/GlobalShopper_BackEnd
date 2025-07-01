package com.sep490.gshop.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestModel {
    private String shippingAddressId;
    private List<String> contactInfo;
    private List<ItemRequestModel> items;
}
