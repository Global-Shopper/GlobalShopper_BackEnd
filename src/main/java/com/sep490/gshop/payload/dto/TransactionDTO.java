package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.TransactionStatus;
import com.sep490.gshop.common.enums.TransactionType;
import com.sep490.gshop.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    private UUID id;
    private String description;
    private TransactionType type;
    private double amount;
    private double balanceBefore;
    private double balanceAfter;
    private TransactionStatus status;
    private CustomerDTO customer;
    private String referenceCode;
    protected long createdAt;
    protected long updatedAt;
}
