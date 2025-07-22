package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.request.QuotationRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface QuotationService {
    QuotationDTO createQuotation(@Valid QuotationRequest input);

    List<QuotationDTO> findAllQuotations();

    QuotationDTO getQuotationById(String quotationId);
}
