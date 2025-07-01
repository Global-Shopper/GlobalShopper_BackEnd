package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.request.PurchaseRequestModel;

public interface PurchaseRequestService {
    PurchaseRequestDTO createPurchaseRequest(PurchaseRequestModel purchaseRequestModel);
}
