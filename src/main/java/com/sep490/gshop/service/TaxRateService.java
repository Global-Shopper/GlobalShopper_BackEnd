package com.sep490.gshop.service;

import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.response.TaxCalculationResult;

import java.util.List;

public interface TaxRateService {
    TaxCalculationResult calculateTaxes(double basePrice, List<TaxRate> taxRates);;
}
