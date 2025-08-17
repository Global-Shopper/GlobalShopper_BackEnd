package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BusinessManagerBusiness;
import com.sep490.gshop.business.WithdrawTicketBusiness;
import com.sep490.gshop.entity.Configuration;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.response.dashboard.DashBoardDetail;
import com.sep490.gshop.payload.response.dashboard.DashBoardResponse;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import com.sep490.gshop.repository.*;
import com.sep490.gshop.repository.specification.CustomSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessManagerBusinessImpl implements BusinessManagerBusiness {

    private final ConfigurationRepository configurationRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final RefundTicketRepository refundTicketRepository;
    private final WithdrawTicketRepository withdrawTicketRepository;

    @Autowired
    public BusinessManagerBusinessImpl(ConfigurationRepository configurationRepository, CustomerRepository customerRepository, ModelMapper modelMapper, PurchaseRequestRepository purchaseRequestRepository, RefundTicketRepository refundTicketRepository, WithdrawTicketBusiness withdrawTicketBusiness, WithdrawTicketRepository withdrawTicketRepository) {
        this.configurationRepository = configurationRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.refundTicketRepository = refundTicketRepository;
        this.withdrawTicketRepository = withdrawTicketRepository;
    }

    @Override
    public Configuration updateServiceFee(Double serviceFee) {
        Configuration config = getConfig();
        if (config == null) {
            config = new Configuration();
        }
        config.setServiceFee(serviceFee);
        return configurationRepository.save(config);
    }

    @Override
    public Configuration getConfig() {
        return configurationRepository.findTopBy();
    }

    @Override
    public Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate) {
        Specification<Customer> spec = CustomSpecification.filterCustomer(
                search, status, startDate, endDate
        );
        return customerRepository.findAll(spec, pageable).map(customer -> modelMapper.map(customer, CustomerDTO.class));
    }

    @Override
    public DashBoardResponse getDashboard(Long startDate, Long endDate) {
        DashBoardResponse prDashBoard = new DashBoardResponse();

        //Dashboard for Purchase Requests
        DashBoardDetail purchaseRequestDetail = new DashBoardDetail();
        long totalPr = purchaseRequestRepository.countByCreatedAtBetween(startDate, endDate);
        List<PRStatus> prStatusList = purchaseRequestRepository.countByStatus(startDate, endDate);
        purchaseRequestDetail.setDashBoardName("PurchaseRequest");
        purchaseRequestDetail.setStatusList(prStatusList);
        purchaseRequestDetail.setTotal(totalPr);
        prDashBoard.addDashBoardDetail(purchaseRequestDetail);
        //Dashboard for RefundTicket
        DashBoardDetail refundTicketDetail = new DashBoardDetail();
        long totalRef = refundTicketRepository.countByCreatedAtBetween(startDate, endDate);
        List<PRStatus> refundStatusList = refundTicketRepository.countByStatus(startDate, endDate);
        refundTicketDetail.setDashBoardName("RefundTicket");
        refundTicketDetail.setStatusList(refundStatusList);
        refundTicketDetail.setTotal(totalRef);
        prDashBoard.addDashBoardDetail(refundTicketDetail);
        //Dashboard for WithdrawTicket
        DashBoardDetail withdrawTicketDetail = new DashBoardDetail();
        long totalWithdraw = withdrawTicketRepository.countByCreatedAtBetween(startDate, endDate);
        List<PRStatus> withdrawStatusList = withdrawTicketRepository.countByStatus(startDate, endDate);
        withdrawTicketDetail.setDashBoardName("WithdrawTicket");
        withdrawTicketDetail.setStatusList(withdrawStatusList);
        withdrawTicketDetail.setTotal(totalWithdraw);
        prDashBoard.addDashBoardDetail(withdrawTicketDetail);

        return prDashBoard;
    }
}
