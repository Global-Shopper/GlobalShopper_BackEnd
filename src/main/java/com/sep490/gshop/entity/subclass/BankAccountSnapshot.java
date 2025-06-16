package com.sep490.gshop.entity.subclass;


import com.sep490.gshop.entity.BankAccount;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BankAccountSnapshot {
    private String bankAccountNumber;
    private String providerName;
    private String accountHolderName;

    public BankAccountSnapshot(BankAccount bankAccount){
        this.bankAccountNumber = bankAccount.getBankAccountNumber();
        this.providerName = bankAccount.getProviderName();
        this.accountHolderName = bankAccount.getAccountHolderName();
    }
}
