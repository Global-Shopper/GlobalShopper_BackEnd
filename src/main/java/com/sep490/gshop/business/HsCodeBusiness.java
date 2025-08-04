package com.sep490.gshop.business;

import com.sep490.gshop.entity.HsCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HsCodeBusiness extends BaseBusinessGeneric<HsCode, String>{
    Page<HsCode> searchByKeyword(String hsCode, String description, Pageable pageable);
}
