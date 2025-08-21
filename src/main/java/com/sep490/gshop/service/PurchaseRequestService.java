package com.sep490.gshop.service;

import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.purchaserequest.OfflineRequest;
import com.sep490.gshop.payload.request.purchaserequest.OnlineRequest;
import com.sep490.gshop.payload.request.purchaserequest.SubRequestModel;
import com.sep490.gshop.payload.request.purchaserequest.UpdateRequestModel;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestModel;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PurchaseRequestService {

    PurchaseRequestResponse<List<RequestItemDTO>> createOnlinePurchaseRequest(OnlineRequest onlineRequest);

    PurchaseRequestResponse<SubRequestDTO> createOfflinePurchaseRequest(OfflineRequest offlineRequest);

    MessageResponse checkPurchaseRequest(String id);

    Page<PurchaseRequestModel> getPurchaseRequests(PurchaseRequestStatus status, String type, RequestType requestType, Pageable pageable);

    MessageResponse createSubRequest(SubRequestModel subRequestModel);

    MessageResponse updatePurchaseRequest(String id, UpdateRequestModel updateRequestModel);

    PurchaseRequestModel getPurchaseRequestById(String id);

    MessageResponse requestCorrection(UUID purchaseRequestId, String correctionNote);
    UpdateRequestModel getPurchaseRequestForEdit(UUID purchaseRequestId);

    PurchaseRequestModel editPurchaseRequest(UUID purchaseRequestId, UpdateRequestModel updateRequestModel);
}
