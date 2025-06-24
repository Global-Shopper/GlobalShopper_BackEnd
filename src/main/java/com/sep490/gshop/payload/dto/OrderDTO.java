package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String id;
    private String trackingNumber;
    private String orderCode;
    private String note;
    private OrderStatus status;
    private double totalPrice;

    private CustomerDTO customer;
    private List<OrderItemDTO> orderItems;
    private AddressSnapshotDTO shippingAddress;

}
