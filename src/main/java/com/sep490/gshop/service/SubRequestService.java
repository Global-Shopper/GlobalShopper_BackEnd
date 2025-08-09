package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.SubUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;

import java.util.UUID;

public interface SubRequestService {
    MessageResponse removeRequestItem(UUID subRequestId, UUID itemId);
    MessageResponse addRequestItem(UUID subRequestId, UUID itemId);
    SubRequestDTO updateSubRequest(UUID subRequestId, SubUpdateRequest subUpdateRequest);
}
