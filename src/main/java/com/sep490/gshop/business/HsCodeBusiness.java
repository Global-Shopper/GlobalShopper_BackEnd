package com.sep490.gshop.business;

import com.sep490.gshop.entity.HsCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HsCodeBusiness extends BaseBusinessGeneric<HsCode, String>{

    Page<HsCode> getAll(Pageable pageable);
    Page<HsCode> searchByHsCodeAndDescriptionForRoots(String hsCodeSearch, String descSearch,Pageable pageable);

    boolean existByHsCode(String hsCode);
}
