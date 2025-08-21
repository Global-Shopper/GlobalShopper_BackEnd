package com.sep490.gshop.business;

import com.sep490.gshop.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface PurchaseRequestBusiness extends BaseBusiness<PurchaseRequest> {
    PurchaseRequest findPurchaseRequestBySubRequestId(UUID subRequestId);

    Page<PurchaseRequest> getAll(Specification<PurchaseRequest> spec, Pageable pageable);
}
