package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.TransactionStatus;
import com.sep490.gshop.common.enums.TransactionType;
import com.sep490.gshop.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("""
        SELECT t FROM Transaction t 
        WHERE t.customer.id = :customerId 
          AND t.createdAt BETWEEN :createdAtAfter AND :createdAtBefore
          AND (:type IS NULL OR t.type = :type)
              AND (:status IS NULL OR t.status = :status)
    """)
    Page<Transaction> findAllByCustomerIdAndCreatedAtBetweenAndType(
            @Param("customerId") UUID customerId,
            @Param("createdAtAfter") long createdAtAfter,
            @Param("createdAtBefore") long createdAtBefore,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            Pageable pageable
    );
    Transaction findByCustomerId(UUID customerId);
    Page<Transaction> findAllByCreatedAtBetween(Long from, Long to, Pageable pageable);
    Page<Transaction> findByCustomerIsNull(Pageable pageable);
    Transaction findByReferenceCode(String referenceCode);
    Transaction findByCustomerIdAndReferenceCode(UUID customerId, String referenceCode);
    @Query("""
    SELECT t FROM Transaction t
    WHERE t.createdAt BETWEEN :from AND :to
      AND (:type IS NULL OR t.type = :type)
      AND (:status IS NULL OR t.status = :status)
""")
    Page<Transaction> findAllByTypeAndCreatedAtBetween(
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("from") Long from,
            @Param("to") Long to,
            Pageable pageable
    );
    Page<Transaction> findAllByCustomerId(UUID customerId, Pageable pageable);
}
