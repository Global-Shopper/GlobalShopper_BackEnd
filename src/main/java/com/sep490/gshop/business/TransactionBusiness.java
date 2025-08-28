package com.sep490.gshop.business;

import com.sep490.gshop.common.enums.TransactionType;
import com.sep490.gshop.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TransactionBusiness extends BaseBusiness<Transaction>{
    Page<Transaction> findTransactionsByCustomerIdPageable(UUID customerId, Pageable pageable);
    Page<Transaction> findTransactionsByCustomerId(UUID customerId,long from, long to, TransactionType type, Pageable pageable);
    Page<Transaction> findTransactionsBetweenDate(long startDate, long endDate, Pageable pageable);
    Page<Transaction> findAll(Pageable pageable);
    Transaction getTransactionByReferenceCode(String referenceCode);
    Transaction findByCustomerId(UUID customerId);
    Transaction findByCustomerAndReferenceCode(UUID customerId, String referenceCode);
    Page<Transaction> findAllBetweenDatesAndFilterStatus(TransactionType type, Long startDate, Long endDate, Pageable pageable);
}
