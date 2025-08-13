package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;

public interface BusinessManagerService {
    ConfigurationDTO updateServiceFee(ServiceFeeConfigModel serviceFee);

    ConfigurationDTO getBusinessManagerConfig();
}
