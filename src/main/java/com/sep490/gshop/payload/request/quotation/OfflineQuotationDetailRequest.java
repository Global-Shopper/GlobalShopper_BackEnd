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


    @DecimalMin(value = "0.0", inclusive = false, message = "basePrice phải lớn hơn 0")
    private double basePrice;

    @Size(max = 500, message = "Note không được vượt quá 500 ký tự")
    private String note;


}
