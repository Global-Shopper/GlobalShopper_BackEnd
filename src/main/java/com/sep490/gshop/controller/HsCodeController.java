package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.payload.dto.HsCodeSearchDTO;
import com.sep490.gshop.payload.dto.HsTreeNodeDTO;
import com.sep490.gshop.payload.request.HsCodeRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.HsCodeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URLConstant.HS_CODE)
@CrossOrigin("*")
@Log4j2
@Validated
public class HsCodeController {

    private final HsCodeService hsCodeService;

    public HsCodeController(HsCodeService hsCodeService) {
        this.hsCodeService = hsCodeService;
    }

    @Operation(summary = "Tìm kiếm mã HS Code theo mô tả, hỗ trợ phân trang và sắp xếp")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<Page<HsTreeNodeDTO>> search(
            @RequestParam(required = false) String hsCode,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        log.info("search() HsCodeController start | desc: {}, description: {}, page: {}, size: {}, direction: {}", hsCode, description, page, size, direction);
        Page<HsTreeNodeDTO> result = hsCodeService.findAll(hsCode, description, page, size, direction);
        log.info("search() HsCodeController end | totalElements: {}", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<HsCodeDTO> createHsCodeIncludeTaxes(@Valid @RequestBody HsCodeRequest hsCodeRequest) {
        log.info("createHsCodeIncludeTaxes() - Start | hsCodeRequest: {}", hsCodeRequest);
        HsCodeDTO hsCodeDTO = hsCodeService.createHsCodeIncludeTaxes(hsCodeRequest);
        log.info("createHsCodeIncludeTaxes() - End | hsCode: {}", hsCodeDTO.getHsCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(hsCodeDTO);
    }

    @GetMapping("/{hsCode}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<HsCodeDTO> getByHsCode(@PathVariable String hsCode) {
        log.info("getByHsCode() - Start | hsCode: {}", hsCode);
        HsCodeDTO hsCodeDTO = hsCodeService.getByHsCode(hsCode);
        log.info("getByHsCode() - End | hsCode: {}", hsCode);
        return ResponseEntity.ok(hsCodeDTO);
    }

    @DeleteMapping("/{hsCode}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<MessageResponse> deleteHsCode(@PathVariable String hsCode) {
        log.info("deleteHsCode() - Start | hsCode: {}", hsCode);
        MessageResponse response = hsCodeService.deleteHsCode(hsCode);
        log.info("deleteHsCode() - End | hsCode: {}, success: {}", hsCode, response.isSuccess());
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }


}
