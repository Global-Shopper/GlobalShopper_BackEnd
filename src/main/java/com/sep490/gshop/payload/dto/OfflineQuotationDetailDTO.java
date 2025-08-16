package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfflineQuotationDetailDTO {
    private UUID id;
    private String requestItemId;
    private double basePrice;
    private double serviceFee;
    private double exchangeRate;
    private List<TaxRateSnapshotDTO> taxRates;
    private String note;
    private Map<String, Double> taxAmounts;
    private double totalTaxAmount;
    private double totalPriceBeforeExchange;
    private double totalVNDPrice;
    private String hsCode;
    private double ServiceRate;
    private int quantity;

}