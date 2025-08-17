package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.BusinessManagerBusiness;
import com.sep490.gshop.entity.Configuration;
import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;
import com.sep490.gshop.payload.response.dashboard.DashBoardResponse;
import com.sep490.gshop.service.BusinessManagerService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class BusinessManagerServiceImpl implements BusinessManagerService {

    private final BusinessManagerBusiness businessManagerBusiness;
    private final ModelMapper modelMapper;

    @Autowired
    public BusinessManagerServiceImpl(BusinessManagerBusiness businessManagerBusiness, ModelMapper modelMapper) {
        this.businessManagerBusiness = businessManagerBusiness;
        this.modelMapper = modelMapper;
    }


    @Override
    public ConfigurationDTO updateServiceFee(ServiceFeeConfigModel serviceFee) {
        try {
            log.info("updateServiceFee() BusinessManagerController Start");
            Configuration config = businessManagerBusiness.updateServiceFee(serviceFee.getServiceFee());
            ConfigurationDTO updatedBusinessManager = modelMapper.map(config, ConfigurationDTO.class);
            log.info("updateServiceFee() BusinessManagerController End | updatedBusinessManager: {}", updatedBusinessManager);
            return updatedBusinessManager;
        } catch (Exception e) {
            log.error("Error updating service fee: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ConfigurationDTO getBusinessManagerConfig() {
        try {
            log.info("getBusinessManagerConfig() BusinessManagerController Start");
            Configuration config = businessManagerBusiness.getConfig();
            ConfigurationDTO updatedBusinessManager = modelMapper.map(config, ConfigurationDTO.class);
            log.info("getBusinessManagerConfig() BusinessManagerController End | updatedBusinessManager: {}", updatedBusinessManager);
            return updatedBusinessManager;
        } catch (Exception e) {
            log.error("getBusinessManagerConfig() BusinessManagerController error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate) {
        try {
            log.info("getBusinessManagerUser() BusinessManagerController Start");
            Page<CustomerDTO> user = businessManagerBusiness.getCustomer(pageable, search, status, startDate, endDate);
            log.info("getBusinessManagerUser() BusinessManagerController End | user: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Error getting customer: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public DashBoardResponse getDashboard(Long startDate, Long endDate) {
        try {
            log.info("getPrDashboard() BusinessManagerController Start | startDate: {}, endDate: {}", startDate, endDate);
            DashBoardResponse prDashBoard = businessManagerBusiness.getDashboard(startDate, endDate);
            log.info("getPrDashboard() BusinessManagerController End | prDashBoard: {}", prDashBoard);
            return prDashBoard;
        } catch (Exception e) {
            log.error("Error getting PR dashboard: {}", e.getMessage());
            throw e;
        }
    }
}
