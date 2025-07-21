package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.HsCodeBusiness;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.service.HsCodeService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class HsCodeServiceImpl implements HsCodeService {
    private final HsCodeBusiness hsCodeBusiness;
    private final ModelMapper modelMapper;

    public HsCodeServiceImpl(HsCodeBusiness hsCodeBusiness, ModelMapper modelMapper) {
        this.hsCodeBusiness = hsCodeBusiness;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<HsCodeDTO> findAll(String description, int page, int size, Sort.Direction direction) {
        log.debug("findAll() Start | description: {}", description);
        try {
            Sort sort = Sort.by(direction, "hsCode"); // hoặc "createdAt" nếu có field này
            Pageable pageable = PageRequest.of(page, size);

            // Giả sử hsCodeBusiness.searchByKeyword dùng (String, Pageable)
            Page<HsCode> pageData = hsCodeBusiness.searchByKeyword(
                    description != null ? description.trim() : null,
                    pageable
            );

            log.debug("findAll() End | found: {}", pageData.getTotalElements());

            // Dùng .map() trực tiếp trả về Page<HsCodeDTO>
            return pageData.map(hs -> modelMapper.map(hs, HsCodeDTO.class));
        } catch (Exception e) {
            log.error("findAll() Exception: {}", e.getMessage(), e);
            throw e;
        }
    }

}
