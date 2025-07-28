package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.service.ShippingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequestMapping(URLConstant.SHIPPING)
@CrossOrigin("*")
public class ShippingController {

    private final ShippingService shippingService;

    @Autowired
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping(path = "/get-token")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getShippingToken(@RequestParam DeliveryCode deliveryCode) {
        log.info("getShippingToken() ShippingController Start | deliveryCode: {}", deliveryCode.getName());
        String token = shippingService.getShippingToken(deliveryCode);
        log.info("getShippingToken() End | token: {}", token);
        return ResponseEntity.ok(token);
    }

}
