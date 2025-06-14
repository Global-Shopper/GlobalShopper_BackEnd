package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.RefundTicketRequest;

import java.util.List;
import java.util.UUID;

public interface RefundTicketService {
    RefundTicketDTO createNewRefundTicketAssignToOrder(RefundTicketRequest refundTicket, UUID orderId);
    RefundTicketDTO createNewRefundTicket(RefundTicketRequest refundTicket);
    RefundTicketDTO getRefundTicketById(UUID id);
    List<RefundTicketDTO> getAllRefundTickets();
    RefundTicketDTO updateRefundTicket(UUID id, RefundTicketRequest request);
    boolean deleteRefundTicket(UUID id);

}
