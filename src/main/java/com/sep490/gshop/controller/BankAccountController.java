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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping(URLConstant.BANK_ACCOUNT)
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;



    @PostMapping
    public ResponseEntity<BankAccountDTO> createBankAccount(@Valid @RequestBody BankAccountRequest request) {
        log.debug("createBankAccount() Start | request: {}", request);
        BankAccountDTO created = bankAccountService.createBankAccount(request);
        log.debug("createBankAccount() End | result: {}", created);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BankAccountDTO> updateBankAccount(
            @PathVariable UUID id,
            @Valid @RequestBody BankAccountUpdateRequest request) {
        log.debug("updateBankAccount() Start | id: {}, request: {}", id, request);
        BankAccountDTO updated = bankAccountService.updateBankAccount(id, request);
        log.debug("updateBankAccount() End | result: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDTO> getBankAccountByCurrent(@PathVariable UUID id) {
        log.debug("getBankAccountByCurrent() Start | id: {}", id);
        BankAccountDTO dto = bankAccountService.getBankAccountByCurrent(id);
        log.debug("getBankAccountByCurrent() End | result: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteBankAccount(@PathVariable UUID id) {
        log.debug("deleteBankAccount() Start | id: {}", id);
        MessageResponse response = bankAccountService.deleteBankAccount(id);
        log.debug("deleteBankAccount() End | result: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountDTO>> getAllBankAccountsByCurrent() {
        log.debug("getAllBankAccountsByCurrent() Start");
        List<BankAccountDTO> list = bankAccountService.getAllBankAccountsByCurrent();
        log.debug("getAllBankAccountsByCurrent() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

}
