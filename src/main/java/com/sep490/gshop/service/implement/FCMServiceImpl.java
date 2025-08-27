package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.FCMBusiness;
import com.sep490.gshop.payload.request.FCMTokenRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.FCMService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class FCMServiceImpl implements FCMService {

    private final FCMBusiness fcmBusiness;

    @Autowired
    public FCMServiceImpl(FCMBusiness fcmBusiness) {
        this.fcmBusiness = fcmBusiness;
    }

    @Override
    public MessageResponse saveToken(FCMTokenRequest request) {
        try {
            log.debug("saveToken() FCMController Start | request: {}", request);
            UUID customerId = AuthUtils.getCurrentUserId();
            MessageResponse response = fcmBusiness.saveToken(request, customerId);
            log.debug("saveToken() End | response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error in saveToken() FCMServiceImpl: {}", e.getMessage());
            throw e;
        }
    }
}
