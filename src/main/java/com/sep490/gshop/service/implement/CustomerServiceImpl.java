package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import com.sep490.gshop.payload.request.CustomerRequest;
import com.sep490.gshop.payload.request.CustomerUpdateRequest;
import com.sep490.gshop.payload.response.CloudinaryResponse;
import com.sep490.gshop.service.CloudinaryService;
import com.sep490.gshop.service.CustomerService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CustomerServiceImpl implements CustomerService {

    private CustomerBusiness customerBusiness;
    private ModelMapper modelMapper;
    private CloudinaryService cloudinaryService;
    private ShippingAddressBusiness shippingAddressBusiness;
    @Autowired
    public CustomerServiceImpl(CustomerBusiness customerBusiness, ModelMapper modelMapper, CloudinaryService cloudinaryService, ShippingAddressBusiness shippingAddressBusiness) {
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
        this.shippingAddressBusiness = shippingAddressBusiness;
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
    public CustomerDTO uploadAvatar(MultipartFile multipartFile) {
        log.debug("uploadAvatar() Start | filename: {}", multipartFile.getOriginalFilename());
        try {
            UUID userId = AuthUtils.getCurrentUserId();
            var customerFound = customerBusiness.getById(userId).orElseThrow(EntityNotFoundException::new);
            FileUploadUtil.AssertAllowedExtension(multipartFile, FileUploadUtil.IMAGE_PATTERN);

            String fileName = FileUploadUtil.formatFileName(multipartFile.getOriginalFilename());

            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(multipartFile, fileName);

            customerFound.setAvatar(cloudinaryResponse.getResponseURL());

            var updatedUser = modelMapper.map(customerBusiness.update(customerFound), CustomerDTO.class);
            log.debug("uploadAvatar() End | avatarUrl: {}", updatedUser.getAvatar());

            return updatedUser;
        }  catch (Exception e) {
            log.error("uploadAvatar() Unexpected Exception | message: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi upload avatar: " + e.getMessage());
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
    public CustomerDTO getCurrentCustomer() {
        try {
            log.debug("getCustomerById() CustomerServiceImpl Start |");
            UUID id = AuthUtils.getCurrentUserId();
            Customer customer = customerBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + id));
            if(customer.getRole() != UserRole.CUSTOMER) {
                throw AppException.builder().code(400).message("Admin xem cái gì ở đây !!").build();
            }
            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
            log.debug("getCustomerById() CustomerServiceImpl End | Customer found: {}", customerDTO);
            return customerDTO;
        } catch (Exception e) {
            log.debug("getCustomerById() CustomerServiceImpl Error | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public CustomerDTO updateCustomer(CustomerUpdateRequest customerRequest) {
        try {
            log.debug("updateCustomer() CustomerServiceImpl Start | customerRequest: {}", customerRequest);
            UUID id = AuthUtils.getCurrentUserId();
            Customer existingCustomer = customerBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + id));

            existingCustomer.setId(id);
            existingCustomer.setName(customerRequest.getName());
            existingCustomer.setPhone(customerRequest.getPhone());
            existingCustomer.setDateOfBirth(customerRequest.getDateOfBirth());
            existingCustomer.setGender(customerRequest.getGender());

            CustomerDTO updatedCustomer = modelMapper.map(customerBusiness.update(existingCustomer), CustomerDTO.class);
            log.debug("updateCustomer() CustomerServiceImpl End | Updated Customer: {}", updatedCustomer);
            return updatedCustomer;
        } catch (Exception e) {
            log.error("updateCustomer() CustomerServiceImpl Error | message: {}", e.getMessage());
            throw e;
        }
    }

    public final boolean findDuplicateMail (String email){
        return customerBusiness.existsByEmail(email);
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
            log.error("deleteCustomer() CustomerServiceImpl Error | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }
}
