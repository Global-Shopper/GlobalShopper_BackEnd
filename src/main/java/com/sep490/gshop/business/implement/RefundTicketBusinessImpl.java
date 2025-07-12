package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.RefundTicketBusiness;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.repository.RefundTicketRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RefundTicketBusinessImpl extends BaseBusinessImpl<RefundTicket, RefundTicketRepository> implements RefundTicketBusiness {
    public RefundTicketBusinessImpl(RefundTicketRepository repository) {
        super(repository);
    }

    @Override
    public RefundTicket getRefundTicketByOrderId(UUID orderId) {
        return repository.findRefundTicketByOrderId(orderId);
    }
}

