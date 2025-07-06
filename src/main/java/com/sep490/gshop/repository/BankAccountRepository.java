package com.sep490.gshop.repository;

import com.sep490.gshop.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByCustomerId(UUID id);
    boolean existsBankAccountByBankAccountNumberAndCustomerId(String bankAccountNumber, UUID customerId);
}
