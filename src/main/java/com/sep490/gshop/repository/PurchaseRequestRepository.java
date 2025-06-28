package com.sep490.gshop.repository;

import com.sep490.gshop.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {
}
