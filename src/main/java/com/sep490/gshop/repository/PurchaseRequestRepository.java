package com.sep490.gshop.repository;

import com.sep490.gshop.entity.PurchaseRequest;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {
    @Query("SELECT ri.purchaseRequest FROM RequestItem ri WHERE ri.subRequest.id = :subRequestId")
    PurchaseRequest findPurchaseRequestBySubRequestId(UUID subRequestId);


    long countByCreatedAtBetween(Long startDate, Long endDate);

    @Query("SELECT pr.status as status, COUNT(pr) as count FROM PurchaseRequest pr " +
            "WHERE pr.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY pr.status")
    List<PRStatus> countByStatus(Long startDate, Long endDate);

    Page<PurchaseRequest> findAll(Specification<PurchaseRequest> specification, Pageable pageable);
}
