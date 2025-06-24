package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.CustomerRequest;
import com.sep490.gshop.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(URLConstant.CUSTOMER)
@Log4j2
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getCustomer() {
        log.info("getCustomer() CustomerController start");
        List<CustomerDTO> result = customerService.getAllCustomers();
        log.info("getCustomer() CustomerController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        log.info("createCustomer() CustomerController start | customerDTO: {}", customerRequest);
        CustomerDTO result = customerService.createCustomer(customerRequest);
        log.info("createCustomer() CustomerController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String id) {
        log.info("getCustomerById() CustomerController start | id: {}", id);
        CustomerDTO result = customerService.getCustomerById(id);
        log.info("getCustomerById() CustomerController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerRequest customerRequest) {
        log.info("updateCustomer() CustomerController start | id: {}, customerRequest: {}", id, customerRequest);
        CustomerDTO result = customerService.updateCustomer(id, customerRequest);
        log.info("updateCustomer() CustomerController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        log.info("deleteCustomer() CustomerController start | id: {}", id);
        boolean check = customerService.deleteCustomer(id);
        if (check) {
            log.info("deleteCustomer() CustomerController end | Customer with id {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } else {
            log.error("deleteCustomer() CustomerController end | Customer with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

}
