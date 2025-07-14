package com.sep490.gshop.business;

import com.sep490.gshop.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TransactionBusiness extends BaseBusiness<Transaction>{
    Page<Transaction> findTransactionsByCustomerId(UUID customerId, Pageable pageable);
    Page<Transaction> findTransactionsBetweenDate(long startDate, long endDate, Pageable pageable);
    Page<Transaction> findAll(Pageable pageable);
}
