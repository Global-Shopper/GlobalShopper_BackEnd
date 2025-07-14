package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TransactionService {
    Page<TransactionDTO> getAll(int page, int size, Sort.Direction direction);
    Page<TransactionDTO> getByCurrentUser(int page, int size, Sort.Direction direction);
    Page<TransactionDTO> getBetweenDates(Long startDate, Long endDate, int page, int size, Sort.Direction direction);
}
