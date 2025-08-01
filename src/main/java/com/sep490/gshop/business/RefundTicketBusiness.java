package com.sep490.gshop.business;

import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.entity.RefundTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RefundTicketBusiness extends BaseBusiness<RefundTicket> {
     RefundTicket getRefundTicketByOrderId(UUID orderId);


    Page<RefundTicket> getAll(UUID userId, RefundStatus status, Pageable pageable);
}
