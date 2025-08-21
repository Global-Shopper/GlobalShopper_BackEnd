package com.sep490.gshop.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sep490.gshop.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    private String id;
    private String trackingNumber;
    private String orderCode;

    private List<String> contactInfo;
    private String seller;
    private String ecommercePlatform;
    private String currency;
    private String region;

    private String note;
    private OrderStatus status;
    private double totalPrice;
    private double shippingFee;

    private CustomerDTO customer;
    private AdminDTO admin;
    private List<OrderItemDTO> orderItems;
    private AddressSnapshotDTO shippingAddress;
    private List<OrderHistoryDTO> history;
    private List<ShipmentTrackingEventDto> shipmentTrackingEvents;
    private FeedbackDTO feedback;

    private long createdAt;
    private long updatedAt;

}
