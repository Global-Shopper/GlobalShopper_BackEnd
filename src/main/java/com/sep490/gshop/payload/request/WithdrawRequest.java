package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawRequest {
    private String reason;
    @NotNull(message = "Nhập số tiền cần rút")
    @Range(min = 0)
    private double amount;
    @NotBlank(message = "Bắt buộc cần phải có ngân hàng nhận tiền")
    private String bankAccountId;
}
