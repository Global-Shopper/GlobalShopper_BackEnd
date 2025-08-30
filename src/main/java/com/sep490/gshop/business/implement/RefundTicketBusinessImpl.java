package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.RefundTicketBusiness;
import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.RefundReason;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.repository.RefundReasonRepository;
import com.sep490.gshop.repository.RefundTicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RefundTicketBusinessImpl extends BaseBusinessImpl<RefundTicket, RefundTicketRepository> implements RefundTicketBusiness {
    private final RefundReasonRepository refundReasonRepository;

    public RefundTicketBusinessImpl(RefundTicketRepository repository, RefundReasonRepository refundReasonRepository) {
        super(repository);
        this.refundReasonRepository = refundReasonRepository;
    }

    @Override
    public RefundTicket getRefundTicketByOrderId(UUID orderId) {
        return repository.getRefundTicketByOrderId(orderId);
    }

    @Override
    public Page<RefundTicket> getAll(UUID userId, RefundStatus status, Pageable pageable) {
        return repository.findAllByUserId(userId, status, pageable);
    }

    @Override
    public List<RefundReason> getAllRefundReasons() {
        return refundReasonRepository.findAll();
    }

    @Override
    public RefundReason createRefundReason(RefundReason reason) {
        return refundReasonRepository.save(reason);
    }

    @Override
    public RefundReason updateRefundReason(RefundReason reason) {
        return refundReasonRepository.save(reason);
    }

    @Override
    public RefundReason getReasonById(UUID id) {
        return refundReasonRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteReasonById(UUID id) {
        refundReasonRepository.deleteById(id);
    }
}

