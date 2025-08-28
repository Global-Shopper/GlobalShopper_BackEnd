package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.TransactionType;
import com.sep490.gshop.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByCustomerIdAndCreatedAtBetweenAndType(UUID customerId, long createdAtAfter, long createdAtBefore, TransactionType type, Pageable pageable);
    Transaction findByCustomerId(UUID customerId);
    Page<Transaction> findAllByCreatedAtBetween(Long from, Long to, Pageable pageable);
    Page<Transaction> findByCustomerIsNull(Pageable pageable);
    Transaction findByReferenceCode(String referenceCode);
    Transaction findByCustomerIdAndReferenceCode(UUID customerId, String referenceCode);
    Page<Transaction> findAllByTypeAndCreatedAtBetween(TransactionType type, Long from, Long to, Pageable pageable);
    Page<Transaction> findAllByCustomerId(UUID customerId, Pageable pageable);
}
