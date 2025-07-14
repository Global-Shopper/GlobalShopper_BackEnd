package com.sep490.gshop.business;

import com.sep490.gshop.entity.RefundTicket;

import java.util.UUID;

public interface RefundTicketBusiness extends BaseBusiness<RefundTicket> {
     RefundTicket getRefundTicketByOrderId(UUID orderId);
}
