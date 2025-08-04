package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HsCodeRequest {
    @NotBlank(message = "HSCode không được để trống")
    @Size(min = 4, max = 8, message = "HSCode có độ dài từ 4 đến 8 ký tự theo quy định của nhà nước")
    private String hsCode;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotBlank(message = "Đơn vị tính không được để trống")
    private String unit;

    private String parentCode;

    @NotNull(message = "Danh sách thuế không được để trống")
    @Size(min = 1, message = "Cần ít nhất 1 thuế cho HSCode")
    private List<TaxRateRequest> taxRates;
}

