package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.QuotationCalculatedDTO;
import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.request.quotation.QuotationRequest;
import com.sep490.gshop.payload.request.quotation.RejectQuotationRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(URLConstant.QUOTATION)
@CrossOrigin("*")
@Log4j2
public class QuotationController {
    @Autowired
    private QuotationService quotationService;

    @PostMapping("calculate")
    public QuotationCalculatedDTO calculateQuotation(@RequestBody @Valid QuotationRequest input) {
        log.debug("calculateQuotation() - Start | subRequestId: {}", input.getSubRequestId());
        try {
            QuotationCalculatedDTO dto = quotationService.calculateQuotationInternal(input);
            log.debug("calculateQuotation() - End | subRequestId: {}", input.getSubRequestId());
            return dto;
        } catch (Exception e) {
            log.error("calculateQuotation() - Exception: {}", e.getMessage(), e);
            throw e;
        }
    }


    @PostMapping
    @Operation(summary = "Tạo báo giá cho sub request (Bao gồm nhiều request item")
    public ResponseEntity<QuotationDTO> createQuotation(@RequestBody @Valid QuotationRequest request) {
        log.info("createQuotation() - Start | subRequestId: {}", request.getSubRequestId());
        QuotationDTO dto = quotationService.createQuotation(request);
        log.info("createQuotation() - End | subRequestId: {}", request.getSubRequestId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reject")
    @Operation(summary = "Từ chối báo giá cho sub request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> rejectQuotation(@RequestBody @Valid RejectQuotationRequest request) {
        log.info("rejectQuotation() - Start | subRequestId: {}", request.getSubRequestId());
        MessageResponse dto = quotationService.rejectQuotation(request);
        log.info("rejectQuotation() - End | subRequestId: {}", request.getSubRequestId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả các báo giá")
    public ResponseEntity<List<QuotationDTO>> findAllQuotations() {
        log.info("findAllQuotations() - Start");
        List<QuotationDTO> dtos = quotationService.findAllQuotations();
        log.info("findAllQuotations() - End | total: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Tìm báo giá theo id")
    public ResponseEntity<QuotationDTO> getQuotationById(@PathVariable("id") String id) {
        log.info("getQuotationById() - Start | quotationId: {}", id);
        QuotationDTO dto = quotationService.getQuotationById(id);
        log.info("getQuotationById() - End | quotationId: {}", id);
        return ResponseEntity.ok(dto);
    }
}
