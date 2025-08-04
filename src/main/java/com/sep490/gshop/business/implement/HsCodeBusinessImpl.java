package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.HsCodeBusiness;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.repository.HsCodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class HsCodeBusinessImpl extends BaseBusinessGenericImpl<HsCode,String, HsCodeRepository> implements HsCodeBusiness {

    protected HsCodeBusinessImpl(HsCodeRepository repository) {
        super(repository);
    }

    @Override
    public Page<HsCode> searchByKeyword(String hsCode, String description, Pageable pageable) {
        return repository.searchByHsCodeAndDescription(hsCode, description, pageable);
    }
}
