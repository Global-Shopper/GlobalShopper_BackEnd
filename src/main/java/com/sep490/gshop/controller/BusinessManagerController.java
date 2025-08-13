package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;
import com.sep490.gshop.service.BusinessManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequestMapping(URLConstant.BUSINESS)
@CrossOrigin("*")
public class BusinessManagerController {

    private final BusinessManagerService businessManagerService;

    @Autowired
    public BusinessManagerController(BusinessManagerService businessManagerService) {
        this.businessManagerService = businessManagerService;
    }

    @PutMapping("/servicefee")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<ConfigurationDTO> updateServiceFee(@RequestBody ServiceFeeConfigModel serviceFee) {
        log.info("updateServiceFee() BusinessManagerController Start | serviceFee: {}", serviceFee);
        ConfigurationDTO updatedBusinessManager = businessManagerService.updateServiceFee(serviceFee);
        log.info("updateServiceFee() BusinessManagerController End | updatedBusinessManager: {}", updatedBusinessManager);
        return ResponseEntity.ok(updatedBusinessManager);
    }

    @GetMapping("/config")
    @PreAuthorize("hasRole('BUSINESS_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ConfigurationDTO> getBusinessManagerConfig() {
        log.info("getBusinessManagerConfig() BusinessManagerController Start");
        ConfigurationDTO config = businessManagerService.getBusinessManagerConfig();
        log.info("getBusinessManagerConfig() BusinessManagerController End | config: {}", config);
        return ResponseEntity.ok(config);
    }

}
