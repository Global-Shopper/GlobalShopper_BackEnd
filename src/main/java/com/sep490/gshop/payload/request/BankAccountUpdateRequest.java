package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountUpdateRequest {
    @Size(min = 5, message = "Số tài khoản phải từ 5 ký tự trở lên")
    private String bankAccountNumber;
    private String providerName;
    private String accountHolderName;
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Ngày hết hạn phải đúng định dạng MM/YY, ví dụ 07/25")
    private String expirationDate;
    private Boolean isDefault;
}
