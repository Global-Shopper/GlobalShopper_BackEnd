package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {
    @NotNull(message = "Nhập số tiền cần thực hiện giao dịch")
    private double balance;
}
