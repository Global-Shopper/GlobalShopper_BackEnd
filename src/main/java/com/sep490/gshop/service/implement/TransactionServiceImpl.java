package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.TransactionBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Transaction;
import com.sep490.gshop.payload.dto.TransactionDTO;
import com.sep490.gshop.service.TransactionService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {
    private TransactionBusiness transactionBusiness;
    private CustomerBusiness customerBusiness;
    private ModelMapper modelMapper;
    @Autowired
    public TransactionServiceImpl(
            TransactionBusiness transactionBusiness,
            CustomerBusiness customerBusiness,
            ModelMapper modelMapper
    ){
        this.transactionBusiness = transactionBusiness;
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
    }
    @Override
    public Page<TransactionDTO> getAll(int page, int size, Sort.Direction direction) {
        log.debug("getAll() Start");
        try {
            Sort sort = Sort.by(direction, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Transaction> transactions = transactionBusiness.findAll(pageable);

            log.debug("getAll() End | total transactions: {}", transactions.getTotalElements());

            return transactions.map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
        } catch (Exception e) {
            log.error("getAll() Exception: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public Page<TransactionDTO> getByCurrentUser(int page, int size, Sort.Direction direction) {
        log.debug("getByCurrentUser() Start");
        try {
            UUID customerId = AuthUtils.getCurrentUserId();
            if (customerId == null) {
                throw AppException.builder()
                        .message("Bạn cần đăng nhập để tiếp tục")
                        .code(401)
                        .build();
            }

            Sort sort = Sort.by(direction, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            var transactions = transactionBusiness.findTransactionsByCustomerId(customerId, pageable);

            log.debug("getByCurrentUser() End | customerId: {}, total transactions: {}", customerId, transactions.getTotalElements());
            return transactions.map(transaction -> modelMapper.map(transaction, TransactionDTO.class)) ;
        } catch (Exception e) {
            log.error("getByCurrentUser() Exception: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<TransactionDTO> getBetweenDates(Long startDate, Long endDate, int page, int size, Sort.Direction direction) {
        log.debug("getBetweenDates() Start | startDate: {}, endDate: {}", startDate, endDate);
        try {
            Sort sort = Sort.by(direction, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            var transactions = transactionBusiness.findTransactionsBetweenDate(startDate, endDate, pageable);
            log.debug("getBetweenDates() End | total transactions: {}", transactions.getTotalElements());
            return transactions.map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
        } catch (Exception e) {
            log.error("getBetweenDates() Exception: {}", e.getMessage());
            throw e;
        }
    }


}
