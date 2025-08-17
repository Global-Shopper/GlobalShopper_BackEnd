package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundTicketRepository extends JpaRepository<RefundTicket, UUID> {

    @Query("SELECT rt FROM RefundTicket rt WHERE rt.order.customer.id = ?1")
    Page<RefundTicket> findByCustomerId(UUID id, Pageable pageable);

    @Query("SELECT rt FROM RefundTicket rt " +
            "WHERE (rt.order.customer.id = :userId OR rt.order.admin.id = :userId) " +
            "AND (:status is null OR rt.status = :status) ")
    Page<RefundTicket> findAllByUserId(@Param("userId") UUID userId, @Param("status") RefundStatus status, Pageable pageable);

    RefundTicket getRefundTicketByOrderId(UUID orderId);

    long countByCreatedAtBetween(Long startDate, Long endDate);

    @Query("SELECT rt.status as status, COUNT(rt) as count FROM RefundTicket rt " +
            "WHERE rt.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY rt.status")
    List<PRStatus> countByStatus(Long startDate, Long endDate);
}
