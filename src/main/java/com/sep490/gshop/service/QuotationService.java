package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.request.QuotationDetailBatchRequest;
import com.sep490.gshop.payload.request.QuotationDetailRequest;
import com.sep490.gshop.payload.request.QuotationInputRequest;

import java.util.List;

public interface QuotationService {
    List<QuotationDetailDTO> createOrUpdateQuotationDetails(QuotationDetailBatchRequest batchInput);
    QuotationDTO getQuotationById(String quotationId);
}
