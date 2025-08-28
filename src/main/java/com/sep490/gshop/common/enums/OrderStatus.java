package com.sep490.gshop.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    AWAITING_PAYMENT("Đơn hàng chờ thanh toán"),
    ORDER_REQUESTED("Đơn hàng đã được đặt"),
    PURCHASED("Đơn hàng đã được mua"),
    IN_TRANSIT("Đơn hàng đang được vận chuyển"),
    ARRIVED_IN_DESTINATION("Đơn hàng đã đến nơi"),
    DELIVERED("Đơn hàng đã được giao"),
    CANCELLED("Đơn hàng đã bị hủy");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
