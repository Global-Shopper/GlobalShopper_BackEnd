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

    @PostMapping("/detail/batch")
    public ResponseEntity<List<QuotationDetailDTO>> createOrUpdateDetailBatch(
            @RequestBody QuotationDetailBatchRequest batchInput
    ) {
        List<QuotationDetailDTO> result = quotationService.createOrUpdateQuotationDetails(batchInput);
        return ResponseEntity.ok(result);
    }
}
