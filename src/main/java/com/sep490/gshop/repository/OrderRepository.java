package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Order> findByAdminIsNull(Pageable pageable);
    Page<Order> findByAdminId(UUID adminId, Pageable pageable);
    Order findByTrackingNumberAndShippingCarrier(String trackingNumber, String shippingCarrier);

    Page<Order> findByCustomerIdAndStatus(UUID id, OrderStatus status, Pageable pageable);

    Page<Order> findByAdminIdAndStatus(UUID id, OrderStatus status, Pageable pageable);
}
