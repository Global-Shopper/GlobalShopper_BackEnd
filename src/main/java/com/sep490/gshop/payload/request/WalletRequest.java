package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {
    @NotNull(message = "Nhập số tiền cần thực hiện giao dịch")
    @Range(min = 10000, max = 50000000, message = "Số tiền nạp phải lớn hơn hoặc bằng 10.000 VNĐ")
    private double balance;
    @NotBlank(message = "Thiếu liên kết trả về")
    private String redirectUri;
}
