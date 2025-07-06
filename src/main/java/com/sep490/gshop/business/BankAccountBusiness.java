package com.sep490.gshop.business;

import com.sep490.gshop.entity.BankAccount;

import java.util.List;
import java.util.UUID;

public interface BankAccountBusiness extends BaseBusiness<BankAccount>{
    List<BankAccount> findBankAccountsByCustomer(UUID id);
    boolean existsBankAccount(String AccountNumber, UUID customerId);
}
