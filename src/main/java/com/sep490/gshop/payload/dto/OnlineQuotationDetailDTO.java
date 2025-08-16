package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineQuotationDetailDTO {
    private String requestItemId;
    private double basePrice;
    private double serviceFee;
    private double totalVNPrice;
    private int quantity;
    private double ServiceRate;
    private double exchangeRate;
}
