package com.sep490.gshop.service;

import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.payload.dto.RefundReasonDTO;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.CreateReasonRequest;
import com.sep490.gshop.payload.request.refund.ProcessRefundModel;
import com.sep490.gshop.payload.request.refund.RefundTicketRequest;
import com.sep490.gshop.payload.request.refund.RejectRefundModel;
import com.sep490.gshop.payload.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RefundTicketService {
    RefundTicketDTO createNewRefundTicket(RefundTicketRequest refundTicket);
    RefundTicketDTO getRefundTicketById(UUID id);
    Page<RefundTicketDTO> getAllRefundTickets(Pageable pageable, RefundStatus status);
    RefundTicketDTO updateRefundTicket(UUID id, RefundTicketRequest request);
    boolean deleteRefundTicket(UUID id);

    RefundTicketDTO processRefundTicket(ProcessRefundModel processRefundModel, String ticketId);

    RefundTicketDTO rejectRefundTicket(String ticketId, RejectRefundModel rejectRefundModel);
    RefundTicketDTO getTicketByOrderId(String orderId);

    List<RefundReasonDTO> getRefundReasons();

    RefundReasonDTO createRefundReason(CreateReasonRequest reason);

    MessageResponse deleteReason(String id);

    RefundReasonDTO changeIsActive(String id);
}
