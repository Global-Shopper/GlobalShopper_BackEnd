package com.sep490.gshop.controller;

import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.request.QuotationDetailBatchRequest;
import com.sep490.gshop.payload.request.QuotationInputRequest;
import com.sep490.gshop.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotation")
@CrossOrigin("*")
public class QuotationController {
    @Autowired
    private QuotationService quotationService;

    @PostMapping("/batch")
    public ResponseEntity<QuotationDTO> createOrUpdateQuotation(
            @RequestBody QuotationInputRequest input
    ) {
        QuotationDTO dto = quotationService.createOrUpdateQuotation(input);
        return ResponseEntity.ok(dto);
    }

}
