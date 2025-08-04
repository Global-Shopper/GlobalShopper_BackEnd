package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaxRateRepository extends JpaRepository<TaxRate, UUID> {
    List<TaxRate> findAllByHsCodeAndRegion(HsCode hsCode, TaxRegion region);
    List<TaxRate> findAllByHsCode(HsCode hsCode);

    boolean existsByHsCodeAndTaxTypeAndRegion(HsCode hsCode, TaxType taxType, TaxRegion region);

}
