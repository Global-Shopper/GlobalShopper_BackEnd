package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.VariantBusiness;
import com.sep490.gshop.entity.Variant;
import com.sep490.gshop.repository.VariantRepository;
import org.springframework.stereotype.Component;

@Component
public class VariantBusinessImpl extends BaseBusinessImpl<Variant, VariantRepository> implements VariantBusiness {
    protected VariantBusinessImpl(VariantRepository repository) {
        super(repository);
    }
}
