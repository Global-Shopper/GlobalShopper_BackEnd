package com.sep490.gshop.payload.request.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineQuotationDetailRequest {
    private String requestItemId;
    private String currency;
    private double basePrice;
}
