package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.service.HsCodeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URLConstant.HS_CODE)
@CrossOrigin("*")
@Log4j2
public class HsCodeController {

    private final HsCodeService hsCodeService;

    public HsCodeController(HsCodeService hsCodeService) {
        this.hsCodeService = hsCodeService;
    }

    @Operation(summary = "Tìm kiếm mã HS Code theo mô tả, hỗ trợ phân trang và sắp xếp")
    @GetMapping("/search")
    public ResponseEntity<Page<HsCodeDTO>> search(
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("search() HsCodeController start | desc: {}, page: {}, size: {}, direction: {}", description, page, size, direction);
        Page<HsCodeDTO> result = hsCodeService.findAll(description, page, size, direction);
        log.info("search() HsCodeController end | totalElements: {}", result.getTotalElements());
        return ResponseEntity.ok(result);
    }


}
