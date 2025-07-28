package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.SubRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationForPurchaseRequestDTO {
    private String id;
    private String note;
    private long expiredDate;
    private double shippingEstimate;
    private double totalPriceEstimate;
}
