package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;
import com.sep490.gshop.payload.response.dashboard.*;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import com.sep490.gshop.service.BusinessManagerService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.*;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    @GetMapping("/customer")
    @PreAuthorize("hasRole('BUSINESS_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerDTO>> getCustomer(@ParameterObject Pageable pageable,
                                                         @RequestParam(name = "search", required = false) String search,
                                                         @RequestParam(name = "status", required = false) Boolean status,
                                                         @RequestParam(name = "startDate") Long startDate,
                                                         @RequestParam(name = "endDate") Long endDate

    ) {
        log.info("getBusinessManagerUser() BusinessManagerController Start");
        Page<CustomerDTO> user = businessManagerService.getCustomer(pageable, search, status, startDate, endDate);
        log.info("getBusinessManagerUser() BusinessManagerController End | user: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('BUSINESS_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<DashBoardResponse> getDashboard(@RequestParam(name = "startDate") Long startDate,
                                                          @RequestParam(name = "endDate") Long endDate) {
        log.info("getPrDashboard() BusinessManagerController Start | startDate: {}, endDate: {}", startDate, endDate);
        DashBoardResponse prDashBoard = businessManagerService.getDashboard(startDate, endDate);
        log.info("getPrDashboard() BusinessManagerController End | prDashBoard: {}", prDashBoard);
        return ResponseEntity.ok(prDashBoard);
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('BUSINESS_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<RevenueResponse> getRevenue(@RequestParam(name = "startDate") Long startDate,
                                                      @RequestParam(name = "endDate") Long endDate) {
        log.info("getRevenue() BusinessManagerController Start | startDate: {}, endDate: {}", startDate, endDate);
        RevenueResponse revenue = businessManagerService.getRevenue(startDate, endDate);
        log.info("getRevenue() BusinessManagerController End | revenue: {}", revenue);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/export-revenue-by-month")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<byte[]> exportRevenueByMonth(@RequestParam int year) throws Exception {
        log.info("Start exportRevenueByMonth | year={}", year);

        var response = businessManagerService.exportRevenueByMonth(year);

        log.info("End exportRevenueByMonth | year={}, fileSize={} bytes", year,
                (response != null ? response.length : 0));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Report_" + year + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response);
    }



}
