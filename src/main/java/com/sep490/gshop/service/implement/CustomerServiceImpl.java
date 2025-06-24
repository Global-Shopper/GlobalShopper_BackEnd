package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.CustomerRequest;
import com.sep490.gshop.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            log.error("getAllCustomers() CustomerServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
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
            log.error("createCustomer() CustomerServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CustomerDTO FindCustomerById(UUID id) {
        try {
            log.debug("createCustomer() CustomerServiceImpl Start | id: {}", id);
            var foundEntity = customerBusiness.getById(id).orElseThrow(() -> new EntityNotFoundException("Customer is not found"));
            log.debug("createCustomer() CustomerServiceImpl End | Customer found: {}", foundEntity);
            return modelMapper.map(foundEntity, CustomerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomerDTO getCustomerById(String id) {
        try {
            log.debug("getCustomerById() CustomerServiceImpl Start | id: {}", id);
            UUID customerId = UUID.fromString(id);
            Customer customer = customerBusiness.getById(customerId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + id));
            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
            log.debug("getCustomerById() CustomerServiceImpl End | Customer found: {}", customerDTO);
            return customerDTO;
        } catch (Exception e) {
            log.debug("getCustomerById() CustomerServiceImpl Error | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public CustomerDTO updateCustomer(String id, CustomerRequest customerRequest) {
        try {
            log.debug("updateCustomer() CustomerServiceImpl Start | id: {}, customerRequest: {}", id, customerRequest);
            UUID customerId = UUID.fromString(id);
            Customer existingCustomer = customerBusiness.getById(customerId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + id));

            existingCustomer.setId(customerId);
            existingCustomer.setName(customerRequest.getName());
            existingCustomer.setEmail(customerRequest.getEmail());
            existingCustomer.setPhone(customerRequest.getPhone());
            existingCustomer.setAddress(customerRequest.getAddress());
            existingCustomer.setAvatar(customerRequest.getAvatar());
            existingCustomer.setRole(UserRole.CUSTOMER);

            CustomerDTO updatedCustomer = modelMapper.map(customerBusiness.update(existingCustomer), CustomerDTO.class);
            log.debug("updateCustomer() CustomerServiceImpl End | Updated Customer: {}", updatedCustomer);
            return updatedCustomer;
        } catch (Exception e) {
            log.error("updateCustomer() CustomerServiceImpl Error | id: {}, message: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteCustomer(String id) {
        try {
            log.debug("deleteCustomer() CustomerServiceImpl Start | id: {}", id);
            UUID customerId = UUID.fromString(id);
            boolean isDeleted = customerBusiness.delete(customerId);
            if (isDeleted) {
                log.debug("deleteCustomer() CustomerServiceImpl End | Customer with id {} deleted successfully", id);
            } else {
                log.error("deleteCustomer() CustomerServiceImpl End | Customer with id {} not found", id);
            }
            return isDeleted;
        } catch (Exception e) {
            log.error("deleteCustomer() CustomerServiceImpl Error | id: {}, message: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
