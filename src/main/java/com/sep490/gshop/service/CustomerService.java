package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.CustomerRequest;
import com.sep490.gshop.payload.request.CustomerUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();
    CustomerDTO createCustomer(CustomerRequest customerRequest);
    CustomerDTO FindCustomerById(UUID id);
    CustomerDTO getCurrentCustomer();
    CustomerDTO updateCustomer(CustomerUpdateRequest customerRequest);
    boolean deleteCustomer(String id);
    CustomerDTO uploadAvatar(MultipartFile multipartFile);
}
