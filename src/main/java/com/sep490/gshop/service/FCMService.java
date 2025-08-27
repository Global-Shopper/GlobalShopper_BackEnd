package com.sep490.gshop.service;

import com.sep490.gshop.payload.request.FCMTokenRequest;
import com.sep490.gshop.payload.response.MessageResponse;

public interface FCMService {
    MessageResponse saveToken(FCMTokenRequest request);
}
