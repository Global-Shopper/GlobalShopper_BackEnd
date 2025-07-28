package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationDetailCalculatedDTO {
    private String requestItemId;
    private double basePrice;
    private double serviceFee;
    private String currency;
    private double exchangeRate;
    private List<TaxRateSnapshotDTO> taxRates;
    private String note;
    private Map<String, Double> taxAmounts;
    private double totalTaxAmount;
    private double totalPriceBeforeExchange;
    private double totalVNDPrice;
}
