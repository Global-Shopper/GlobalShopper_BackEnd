package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.request.QuotationRequest;
import com.sep490.gshop.service.QuotationService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(URLConstant.QUOTATION)
@CrossOrigin("*")
@Log4j2
public class QuotationController {
    @Autowired
    private QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationDTO> createQuotation(@RequestBody @Valid QuotationRequest request) {
        log.info("createQuotation() - Start | subRequestId: {}", request.getSubRequestId());
        QuotationDTO dto = quotationService.createQuotation(request);
        log.info("createQuotation() - End | subRequestId: {}", request.getSubRequestId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<QuotationDTO>> findAllQuotations() {
        log.info("findAllQuotations() - Start");
        List<QuotationDTO> dtos = quotationService.findAllQuotations();
        log.info("findAllQuotations() - End | total: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationDTO> getQuotationById(@PathVariable("id") String id) {
        log.info("getQuotationById() - Start | quotationId: {}", id);
        QuotationDTO dto = quotationService.getQuotationById(id);
        log.info("getQuotationById() - End | quotationId: {}", id);
        return ResponseEntity.ok(dto);
    }
}
