package com.sep490.gshop.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sep490.gshop.payload.dto.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseRequestModel {
    private String id;
    private ShippingAddressDTO shippingAddress;
    private String status;
    private AdminDTO admin;
    private CustomerDTO customer;
    private String requestType;
    private List<RequestItemDTO> items;
    private List<SubRequestDTO> subRequests;
}
