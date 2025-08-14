package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.RefundTicketBusiness;
import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.repository.RefundTicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RefundTicketBusinessImpl extends BaseBusinessImpl<RefundTicket, RefundTicketRepository> implements RefundTicketBusiness {
    public RefundTicketBusinessImpl(RefundTicketRepository repository) {
        super(repository);
    }

    @Override
    public RefundTicket getRefundTicketByOrderId(UUID orderId) {
        return repository.getRefundTicketByOrderId(orderId);
    }

    @Override
    public Page<RefundTicket> getAll(UUID userId, RefundStatus status, Pageable pageable) {
        return repository.findAllByUserId(userId, status, pageable);
    }
}

