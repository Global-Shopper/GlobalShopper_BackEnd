package com.sep490.gshop.payload.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuotationDetailDTO {
    private String id;
    private String requestItemId;
    private double basePrice;
    private double serviceFee;
    private double shippingEstimate;
    private List<TaxRateSnapshotDTO> taxRates;
    private String note;
}