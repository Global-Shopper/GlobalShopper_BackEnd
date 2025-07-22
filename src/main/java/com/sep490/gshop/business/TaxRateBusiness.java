package com.sep490.gshop.business;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;

import java.util.List;

public interface TaxRateBusiness extends BaseBusiness<TaxRate>{
    List<TaxRate> findTaxRateHsCodeAndRegion(HsCode hsCode, TaxRegion region);
}
