package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;
import com.sep490.gshop.payload.response.dashboard.DashBoardResponse;
import com.sep490.gshop.payload.response.dashboard.MonthlyDashboard;
import com.sep490.gshop.payload.response.dashboard.MonthlyRevenue;
import com.sep490.gshop.payload.response.dashboard.RevenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface BusinessManagerService {
    ConfigurationDTO updateServiceFee(ServiceFeeConfigModel serviceFee);

    ConfigurationDTO getBusinessManagerConfig();

    Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate);

    DashBoardResponse getDashboard(Long startDate, Long endDate);

    RevenueResponse getRevenue(Long startDate, Long endDate);

    List<MonthlyRevenue> getRevenueSummaryByMonth(int year);

    List<MonthlyDashboard> getDashboardByYear(int year);

    byte[] exportRevenueByMonth(int year) throws IOException;
}
