package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxRateCreateAndUpdateRequest {
    @NotNull(message = "Khu vực không được để trống")
    private TaxRegion region;

    @NotNull(message = "Loại thuế không được để trống")
    private TaxType taxType;

    @NotNull(message = "Thuế suất không được để trống")
    @DecimalMin(value = "0.0", message = "Thuế suất phải lớn hơn hoặc bằng 0")
    private Double rate;
    @NotBlank(message = "Tên loại thuế không được bỏ trống")
    private String taxName;
    @NotNull(message = "HSCode không được bỏ trống")
    @Size(min = 4, max = 8, message = "HSCode có độ dài từ 4 đến 8 ký tự theo quy định của nhà nước")
    private String hsCode;

}
