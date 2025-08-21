package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.PurchaseRequestBusiness;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.entity.PurchaseRequest;
import com.sep490.gshop.repository.PurchaseRequestRepository;
import com.sep490.gshop.repository.specification.CustomSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PurchaseRequestBusinessImpl extends BaseBusinessImpl<PurchaseRequest, PurchaseRequestRepository> implements PurchaseRequestBusiness {
    public PurchaseRequestBusinessImpl(PurchaseRequestRepository repository) {
        super(repository);
    }

    @Override
    public PurchaseRequest findPurchaseRequestBySubRequestId(UUID subRequestId) {
        return repository.findPurchaseRequestBySubRequestId(subRequestId);
    }

    @Override
    public Page<PurchaseRequest> getAll(Specification<PurchaseRequest> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }
}
