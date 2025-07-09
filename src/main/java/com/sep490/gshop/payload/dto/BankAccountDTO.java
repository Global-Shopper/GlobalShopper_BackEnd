package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDTO {
    private UUID id;
    private String bankAccountNumber;
    private String providerName;
    private String accountHolderName;
    private String expirationDate;
    private boolean isDefault;

}
