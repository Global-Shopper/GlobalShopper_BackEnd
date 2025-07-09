package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {
    @NotBlank(message = "Số tài khoản không được để trống")
    @Size(min = 5, message = "Số tài khoản phải từ ký tự")
    private String bankAccountNumber;

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String providerName;

    @NotBlank(message = "Tên chủ tài khoản không được để trống")
    private String accountHolderName;

    @NotBlank(message = "Ngày hết hạn không được để trống")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Ngày hết hạn phải đúng định dạng MM/YY, ví dụ 07/25")
    private String expirationDate;
    private boolean isDefault;
}
