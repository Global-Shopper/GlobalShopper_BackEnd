package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.BankAccountDTO;
import com.sep490.gshop.payload.request.BankAccountRequest;
import com.sep490.gshop.payload.request.BankAccountUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping(URLConstant.BANK_ACCOUNT)
@CrossOrigin("*")
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BankAccountDTO> createBankAccount(@Valid @RequestBody BankAccountRequest request) {
        log.info("createBankAccount() Start | request: {}", request);
        BankAccountDTO created = bankAccountService.createBankAccount(request);
        log.info("createBankAccount() End | result: {}", created);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BankAccountDTO> updateBankAccount(
            @PathVariable UUID id,
            @Valid @RequestBody BankAccountUpdateRequest request) {
        log.info("updateBankAccount() Start | id: {}, request: {}", id, request);
        BankAccountDTO updated = bankAccountService.updateBankAccount(id, request);
        log.info("updateBankAccount() End | result: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BankAccountDTO> getBankAccountByCurrent(@PathVariable UUID id) {
        log.info("getBankAccountByCurrent() Start | id: {}", id);
        BankAccountDTO dto = bankAccountService.getBankAccountByCurrent(id);
        log.info("getBankAccountByCurrent() End | result: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<MessageResponse> deleteBankAccount(@PathVariable UUID id) {
        log.info("deleteBankAccount() Start | id: {}", id);
        MessageResponse response = bankAccountService.deleteBankAccount(id);
        log.info("deleteBankAccount() End | result: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BankAccountDTO>> getAllBankAccountsByCurrent() {
        log.info("getAllBankAccountsByCurrent() Start");
        List<BankAccountDTO> list = bankAccountService.getAllBankAccountsByCurrent();
        log.info("getAllBankAccountsByCurrent() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

}
