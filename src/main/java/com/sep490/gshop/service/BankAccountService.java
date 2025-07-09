package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.BankAccountDTO;
import com.sep490.gshop.payload.request.BankAccountRequest;
import com.sep490.gshop.payload.request.BankAccountUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {
    BankAccountDTO createBankAccount(BankAccountRequest bankAccountRequest);
    BankAccountDTO updateBankAccount(UUID id, BankAccountUpdateRequest bankAccountRequest);
    BankAccountDTO getBankAccountByCurrent(UUID bankAccountId);
    MessageResponse deleteBankAccount(UUID bankAccountId);
    List<BankAccountDTO> getAllBankAccountsByCurrent();
}
