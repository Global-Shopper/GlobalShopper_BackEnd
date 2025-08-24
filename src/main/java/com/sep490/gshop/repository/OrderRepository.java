package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Order> findByAdminIsNull(Pageable pageable);
    Page<Order> findByAdminId(UUID adminId, Pageable pageable);
    Order findByTrackingNumberAndShippingCarrier(String trackingNumber, String shippingCarrier);

    Page<Order> findByCustomerIdAndStatus(UUID id, OrderStatus status, Pageable pageable);

    Page<Order> findByAdminIdAndStatus(UUID id, OrderStatus status, Pageable pageable);

    Order getOrderByAdminId(UUID adminId);

    List<Order> findByUpdatedAtBetweenAndStatus(Long start, Long end, OrderStatus status);

    @Query("SELECT order.status AS status, COUNT(order) AS count " +
            "FROM Order order " +
            "WHERE order.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY order.status")
    List<PRStatus> countByStatus(Long startDate, Long endDate);

    long countByCreatedAtBetween(Long startDate, Long endDate);
}

