package com.sep490.gshop.business;

import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.payload.dto.RefundTicketDTO;

import java.util.UUID;

public interface RefundTicketBusiness extends BaseBusiness<RefundTicket> {
    public RefundTicketDTO getRefundTicketByOrderId(UUID orderId);
}
