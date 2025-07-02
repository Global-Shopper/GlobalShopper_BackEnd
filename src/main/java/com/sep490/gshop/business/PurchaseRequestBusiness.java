package com.sep490.gshop.business;

import com.sep490.gshop.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PurchaseRequestBusiness extends BaseBusiness<PurchaseRequest> {
    Page<PurchaseRequest> findByCustomerId(UUID userId, Pageable pageable);

    Page<PurchaseRequest> findUnassignedRequests(Pageable pageable);

    Page<PurchaseRequest> findAssignedRequestsByAdminId(UUID userId, Pageable pageable);
}
