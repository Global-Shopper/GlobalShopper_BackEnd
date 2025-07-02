package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.PurchaseRequestModel;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PurchaseRequestService {

    PurchaseRequestResponse<List<RequestItemDTO>> createOnlinePurchaseRequest(PurchaseRequestModel purchaseRequestModel);

    PurchaseRequestResponse<SubRequestDTO> createOfflinePurchaseRequest(PurchaseRequestModel purchaseRequestModel);

    MessageResponse checkPurchaseRequest(String id);

    Page<PurchaseRequestDTO> getPurchaseRequests(int page, int size, String type);
}
