package com.sep490.gshop.controller;

import com.sep490.gshop.common.URLConstant;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.CustomerRequest;
import com.sep490.gshop.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CustomerDTO> createCustomer(@Valid CustomerRequest customerRequest) {
        log.info("createCustomer() CustomerController start | customerDTO: {}", customerRequest);
        CustomerDTO result = customerService.createCustomer(customerRequest);
        log.info("createCustomer() CustomerController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

}
