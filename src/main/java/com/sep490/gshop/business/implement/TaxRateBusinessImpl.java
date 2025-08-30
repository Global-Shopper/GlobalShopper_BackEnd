package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.TaxRateBusiness;
import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.repository.TaxRateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaxRateBusinessImpl extends BaseBusinessImpl<TaxRate, TaxRateRepository> implements TaxRateBusiness {
    protected TaxRateBusinessImpl(TaxRateRepository repository) {
        super(repository);
    }

    @Override
    public List<TaxRate> findTaxRateHsCodeAndRegion(HsCode hsCode, TaxRegion region) {
        return repository.findAllByHsCodeAndRegion(hsCode, region);
    }

    @Override
    public List<TaxRate> findAllByHsCode(HsCode hsCode) {
        return repository.findAllByHsCode(hsCode);
    }

    @Override
    public boolean existsByHsCodeAndRegionAndTaxType(HsCode hsCode, TaxRegion region, TaxType taxType) {
        return repository.existsByHsCodeAndTaxTypeAndRegion(hsCode, taxType, region);
    }

    @Override
    public Optional<TaxRate> findByHsCodeAndRegionAndTaxType(HsCode hsCode, TaxRegion region, TaxType taxType) {
        return repository.getByHsCodeAndRegionAndTaxType(hsCode, region, taxType);
    }


}
