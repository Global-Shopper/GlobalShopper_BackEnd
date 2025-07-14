package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.TransactionBusiness;
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
    public Page<Transaction> findTransactionsByCustomerId(UUID customerId, Pageable pageable) {
        return repository.findAllByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Transaction> findTransactionsBetweenDate(long startDate, long endDate, Pageable pageable) {
        return repository.findAllByCreatedAtBetween(startDate, endDate, pageable);
    }

    @Override
    public Page<Transaction> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
