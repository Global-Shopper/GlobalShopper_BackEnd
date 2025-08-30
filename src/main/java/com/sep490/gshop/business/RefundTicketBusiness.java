package com.sep490.gshop.business;

import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.entity.RefundReason;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.payload.dto.RefundReasonDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RefundTicketBusiness extends BaseBusiness<RefundTicket> {
     RefundTicket getRefundTicketByOrderId(UUID orderId);
     Page<RefundTicket> getAll(UUID userId, RefundStatus status, Pageable pageable);
     List<RefundReason> getAllRefundReasons();
     RefundReason createRefundReason(RefundReason reason);
     RefundReason updateRefundReason(RefundReason reason);
     RefundReason getReasonById(UUID id);
     void deleteReasonById(UUID id);
}
