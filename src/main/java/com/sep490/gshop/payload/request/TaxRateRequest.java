package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxRateRequest {
    @NotNull(message = "Khu vực không được để trống")
    private TaxRegion region;

    @NotNull(message = "Loại thuế không được để trống")
    private TaxType taxType;

    @NotNull(message = "Thuế suất không được để trống")
    @DecimalMin(value = "0.0", message = "Thuế suất phải lớn hơn hoặc bằng 0")
    private Double rate;

    private String taxName;
}

