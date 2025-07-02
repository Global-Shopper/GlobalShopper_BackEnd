package com.sep490.gshop.repository;

import com.sep490.gshop.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {
    Page<PurchaseRequest> findByCustomerId(UUID userId, Pageable pageable);
    Page<PurchaseRequest> findByAdminIsNull(Pageable pageable);
    Page<PurchaseRequest> findByAdminId(UUID userId, Pageable pageable);

}
