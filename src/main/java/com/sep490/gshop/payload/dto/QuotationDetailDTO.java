package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.TaxRegion;
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
public class QuotationDetailDTO {
    private String id;
    private String requestItemId;
    private double basePrice;
    private double serviceFee;
    private String note;
    private Map<String, Double> taxAmounts;
    private double totalVNDPrice;

}