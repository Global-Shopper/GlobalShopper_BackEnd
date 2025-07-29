package com.sep490.gshop.business;

import com.sep490.gshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderBusiness extends BaseBusiness<Order>{
    Page<Order> getOrdersByCustomerId(UUID id, Pageable pageable);

    Page<Order> getUnassignedOrders(Pageable pageable);

    Page<Order> getAssignedOrdersByAdminId(UUID id, Pageable pageable);

    Order findByTrackingNumber(String trackingNumber, String deliveryCode);
}
