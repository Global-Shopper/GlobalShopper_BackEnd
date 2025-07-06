package com.sep490.gshop.payload.dto;

import com.sep490.gshop.entity.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private String id;
    private double balance;
    private List<BankAccount> bankAccounts;
}
