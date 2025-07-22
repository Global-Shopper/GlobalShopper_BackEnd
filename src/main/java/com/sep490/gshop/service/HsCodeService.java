package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.HsCodeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface HsCodeService {
    Page<HsCodeDTO> findAll(String description, int page, int size, Sort.Direction direction);

}
