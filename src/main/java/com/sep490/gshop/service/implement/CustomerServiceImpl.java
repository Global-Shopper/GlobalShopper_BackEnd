package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.common.UserRole;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.Wallet;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.CustomerRequest;
import com.sep490.gshop.service.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CustomerServiceImpl implements CustomerService {

    private CustomerBusiness customerBusiness;
    private ModelMapper modelMapper;

    @Autowired
    public CustomerServiceImpl(CustomerBusiness customerBusiness, ModelMapper modelMapper) {
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        try {
            log.debug("getAllCustomers() CustomerServiceImpl Start");
            List<CustomerDTO> customers = customerBusiness.getAll().stream()
                    .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                    .toList();
            log.debug("getAllCustomers() CustomerServiceImpl End | Customers size: {}", customers.size());
            return customers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve customers", e);
        }
    }

    @Override
    public CustomerDTO createCustomer(CustomerRequest customerRequest) {
        try {
            log.debug("createCustomer() CustomerServiceImpl Start | customerRequest: {}", customerRequest);
            Customer customerEntity = modelMapper.map(customerRequest, Customer.class);
            customerEntity.setId(UUID.randomUUID());
            customerEntity.setRole(UserRole.CUSTOMER);
            CustomerDTO createdCustomer = modelMapper.map(customerBusiness.create(customerEntity), CustomerDTO.class);
            log.debug("createCustomer() CustomerServiceImpl End | Created Customer: {}", createdCustomer);
            return createdCustomer;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer", e);
        }
    }
}
