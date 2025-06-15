package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.CustomerRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();

    CustomerDTO createCustomer(CustomerRequest customerRequest);

    CustomerDTO FindCustomerById(UUID id);
}
