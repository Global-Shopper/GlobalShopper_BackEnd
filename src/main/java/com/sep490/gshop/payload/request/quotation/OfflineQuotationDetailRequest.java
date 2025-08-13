package com.sep490.gshop.payload.request.quotation;

import com.sep490.gshop.common.enums.TaxRegion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfflineQuotationDetailRequest {

    @NotNull(message = "requestItemId không được để trống")
    private String requestItemId;

    @NotNull(message = "hsCodeId không được để trống")
    @Size(max = 8, message = "HS Code chỉ có 8 kí tự")
    private String hsCodeId;

    @NotNull(message = "region không được để trống")
    private TaxRegion region;

    @DecimalMin(value = "0.0", inclusive = false, message = "basePrice phải lớn hơn 0")
    private double basePrice;

    @DecimalMin(value = "0.0", message = "serviceFee phải lớn hơn hoặc bằng 0")
    private double serviceFee;

    @Size(max = 500, message = "Note không được vượt quá 500 ký tự")
    private String note;

    @NotBlank(message = "currency không được để trống")
    private String currency;

}
