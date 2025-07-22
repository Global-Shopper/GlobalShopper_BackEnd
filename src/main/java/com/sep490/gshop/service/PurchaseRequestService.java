package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
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
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PurchaseRequestService {

    PurchaseRequestResponse<List<RequestItemDTO>> createOnlinePurchaseRequest(OnlineRequest onlineRequest);

    PurchaseRequestResponse<SubRequestDTO> createOfflinePurchaseRequest(OfflineRequest offlineRequest);

    MessageResponse checkPurchaseRequest(String id);

    Page<PurchaseRequestModel> getPurchaseRequests(int page, int size, Sort.Direction direction, String type);

    MessageResponse createSubRequest(SubRequestModel subRequestModel);

    MessageResponse updatePurchaseRequest(String id, UpdateRequestModel updateRequestModel);

    PurchaseRequestModel getPurchaseRequestById(String id);
}
