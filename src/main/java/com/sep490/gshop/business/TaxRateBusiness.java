package com.sep490.gshop.business;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxRateBusiness extends BaseBusiness<TaxRate>{
    List<TaxRate> findTaxRateHsCodeAndRegion(HsCode hsCode, TaxRegion region);
    List<TaxRate> findAllByHsCode(HsCode hsCode);
    boolean existsByHsCodeAndRegionAndTaxType(HsCode hsCode, TaxRegion region, TaxType taxType);
    Optional<TaxRate> findByHsCodeAndRegionAndTaxType(HsCode hsCode, TaxRegion region, TaxType taxType);
}
