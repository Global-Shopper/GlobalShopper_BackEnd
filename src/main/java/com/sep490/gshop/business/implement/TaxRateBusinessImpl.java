package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.TaxRateBusiness;
import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.repository.TaxRateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class TaxRateBusinessImpl extends BaseBusinessImpl<TaxRate, TaxRateRepository> implements TaxRateBusiness {
    protected TaxRateBusinessImpl(TaxRateRepository repository) {
        super(repository);
    }

    @Override
    public List<TaxRate> findTaxRateHsCodeAndRegion(HsCode hsCode, TaxRegion region) {
        return repository.findAllByHsCodeAndRegion(hsCode, region);
    }
}
