package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.PurchaseRequestBusiness;
import com.sep490.gshop.entity.PurchaseRequest;
import com.sep490.gshop.repository.PurchaseRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PurchaseRequestBusinessImpl extends BaseBusinessImpl<PurchaseRequest, PurchaseRequestRepository> implements PurchaseRequestBusiness {
    public PurchaseRequestBusinessImpl(PurchaseRequestRepository repository) {
        super(repository);
    }

    @Override
    public Page<PurchaseRequest> findByCustomerId(UUID userId, Pageable pageable) {
        return repository.findByCustomerId(userId, pageable);
    }

    @Override
    public Page<PurchaseRequest> findUnassignedRequests(Pageable pageable) {
        return repository.findByAdminIsNull(pageable);
    }

    @Override
    public Page<PurchaseRequest> findAssignedRequestsByAdminId(UUID userId, Pageable pageable) {
        return repository.findByAdminId(userId, pageable);
    }

    @Override
    public PurchaseRequest findPurchaseRequestBySubRequestId(UUID subRequestId) {
        return repository.findPurchaseRequestBySubRequestId(subRequestId);
    }
}
