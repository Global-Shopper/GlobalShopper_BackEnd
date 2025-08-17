package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusinessManagerService {
    ConfigurationDTO updateServiceFee(ServiceFeeConfigModel serviceFee);

    ConfigurationDTO getBusinessManagerConfig();

    Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate);
}
