package com.sep490.gshop.business;

import com.sep490.gshop.entity.FCMToken;
import com.sep490.gshop.payload.request.FCMTokenRequest;
import com.sep490.gshop.payload.response.MessageResponse;

import java.util.UUID;

public interface FCMBusiness extends BaseBusiness<FCMToken> {

    MessageResponse saveToken(FCMTokenRequest request, UUID customerId);
}
