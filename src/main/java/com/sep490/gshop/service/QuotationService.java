package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.OnlineQuotationDTO;
import com.sep490.gshop.payload.dto.QuotationCalculatedDTO;
import com.sep490.gshop.payload.dto.OfflineQuotationDTO;
import com.sep490.gshop.payload.request.quotation.OffineQuotationRequest;
import com.sep490.gshop.payload.request.quotation.OnlineQuotationRequest;
import com.sep490.gshop.payload.request.quotation.RejectQuotationRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface QuotationService {
    OfflineQuotationDTO createOfflineQuotation(@Valid OffineQuotationRequest input);

    List<OfflineQuotationDTO> findAllQuotations();

    OfflineQuotationDTO getQuotationById(String quotationId);

    QuotationCalculatedDTO calculateOfflineQuotationInternal(OffineQuotationRequest input);

    MessageResponse rejectQuotation(@Valid RejectQuotationRequest rejectQuotationRequest);

    OnlineQuotationDTO createOnlineQuotation(@Valid OnlineQuotationRequest request);


}
