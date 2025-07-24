package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.TransactionDTO;
import com.sep490.gshop.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(URLConstant.TRANSACTION)
@CrossOrigin("*")
@Log4j2
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @GetMapping("/transactions")
    @Operation(summary = "Lấy danh sách tất cả giao dịch với phân trang và sắp xếp")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam Sort.Direction direction) {
        log.info("GET /transactions - Bắt đầu lấy danh sách tất cả giao dịch");
        Page<TransactionDTO> transactions = transactionService.getAll(page, size, direction);
        log.info("GET /transactions - Kết thúc lấy danh sách, tổng số: {}", transactions.getTotalElements());
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/transactions/current-user")
    @Operation(summary = "Lấy danh sách giao dịch của người dùng hiện tại, có phân trang và sắp xếp")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByCurrentUser(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam Sort.Direction direction) {
        log.info("GET /transactions/current-user - Bắt đầu lấy giao dịch của người dùng hiện tại");
        Page<TransactionDTO> transactions = transactionService.getByCurrentUser(page, size, direction);
        log.info("GET /transactions/current-user - Kết thúc lấy giao dịch, tổng số: {}", transactions.getTotalElements());
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/transactions/between-dates")
    @Operation(summary = "Lấy giao dịch trong khoảng thời gian cụ thể (startDate đến endDate) với phân trang và sắp xếp")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsBetweenDates(
            @RequestParam Long startDate,
            @RequestParam Long endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam Sort.Direction direction) {
        log.info("GET /transactions/between-dates - startDate: {}, endDate: {}", startDate, endDate);
        Page<TransactionDTO> transactions = transactionService.getBetweenDates(startDate, endDate, page, size, direction);
        log.info("GET /transactions/between-dates - total transactions: {}", transactions.getTotalElements());
        return ResponseEntity.ok(transactions);
    }
}
