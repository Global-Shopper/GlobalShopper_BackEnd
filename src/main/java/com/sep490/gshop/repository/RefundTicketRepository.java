package com.sep490.gshop.repository;

import com.sep490.gshop.entity.RefundTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundTicketRepository extends JpaRepository<RefundTicket, UUID> {
     RefundTicket findRefundTicketByOrderId(UUID orderId);
}
