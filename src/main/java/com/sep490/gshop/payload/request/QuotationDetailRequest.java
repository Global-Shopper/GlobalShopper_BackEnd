package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.entity.HsCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationDetailRequest {
    private String subRequestId;
    private String requestItemId;
    private String hsCodeId;
    private TaxRegion region;
    private double basePrice;
    private double serviceFee;
    private double shippingEstimate;
    private String note;
}