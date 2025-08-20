package com.sep490.gshop.business;

import com.sep490.gshop.entity.Configuration;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.response.dashboard.DashBoardResponse;
import com.sep490.gshop.payload.response.dashboard.RevenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BusinessManagerBusiness {
    Configuration updateServiceFee(Double serviceFee);

    Configuration getConfig();

    Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate);

    DashBoardResponse getDashboard(Long startDate, Long endDate);

    List<Order> getRevenue(Long startDate, Long endDate);
}
