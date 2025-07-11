package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.entity.subclass.BankAccountSnapshot;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawTicketDTO {
    private UUID id;
    private String bankingBill;
    private String reason;
    private String denyReason;
    private double amount;
    private RefundStatus status;
    private BankAccountSnapshot bankAccount;
}
