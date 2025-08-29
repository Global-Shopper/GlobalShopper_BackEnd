package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.TransactionBusiness;
import com.sep490.gshop.common.enums.TransactionStatus;
import com.sep490.gshop.common.enums.TransactionType;
import com.sep490.gshop.entity.Transaction;
import com.sep490.gshop.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionBusinessImpl extends BaseBusinessImpl<Transaction, TransactionRepository> implements TransactionBusiness {
    protected TransactionBusinessImpl(TransactionRepository repository) {
        super(repository);
    }

    @Override
    public Page<Transaction> findTransactionsByCustomerIdPageable(UUID customerId, Pageable pageable) {
        return repository.findAllByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Transaction> findTransactionsByCustomerId(UUID customerId, long from, long to, TransactionType type, TransactionStatus status, Pageable pageable) {
        return repository.findAllByCustomerIdAndCreatedAtBetweenAndType(customerId, from, to, type, status, pageable);

    }
    @Override
    public Page<Transaction> findTransactionsBetweenDate(long startDate, long endDate, Pageable pageable) {
        return repository.findAllByCreatedAtBetween(startDate, endDate, pageable);
    }

    @Override
    public Page<Transaction> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Transaction getTransactionByReferenceCode(String referenceCode) {
        return repository.findByReferenceCode(referenceCode);
    }

    @Override
    public Transaction findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }

    @Override
    public Transaction findByCustomerAndReferenceCode(UUID customerId, String referenceCode) {
        return repository.findByCustomerIdAndReferenceCode(customerId, referenceCode);
    }

    @Override
    public Page<Transaction> findAllBetweenDatesAndFilterStatus(TransactionType type, TransactionStatus status, Long startDate, Long endDate, Pageable pageable) {
        return repository.findAllByTypeAndCreatedAtBetween(type, status, startDate, endDate, pageable);
    }
}
