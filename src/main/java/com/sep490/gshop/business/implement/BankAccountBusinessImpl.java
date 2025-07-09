package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BankAccountBusiness;
import com.sep490.gshop.entity.BankAccount;
import com.sep490.gshop.repository.BankAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BankAccountBusinessImpl extends BaseBusinessImpl<BankAccount, BankAccountRepository> implements BankAccountBusiness {
    protected BankAccountBusinessImpl(BankAccountRepository repository) {
        super(repository);
    }

    @Override
    public List<BankAccount> findBankAccountsByCustomer(UUID id) {
        return repository.findByCustomerId(id);
    }

    @Override
    public boolean existsBankAccount(String AccountNumber, UUID customerId) {
        return repository.existsBankAccountByBankAccountNumberAndCustomerId(AccountNumber, customerId);
    }
}
