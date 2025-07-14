package com.sep490.gshop.repository;

import com.sep490.gshop.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByCustomerId(UUID userId, Pageable pageable);
    Page<Transaction> findAllByCreatedAtBetween(Long from, Long to, Pageable pageable);
    Page<Transaction> findByCustomerIsNull(Pageable pageable);
}
