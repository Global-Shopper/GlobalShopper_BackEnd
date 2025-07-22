package com.sep490.gshop.payload.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TaxCalculationResult {
    private Map<String, Double> taxAmounts = new HashMap<>();
    private double totalTax;
}
