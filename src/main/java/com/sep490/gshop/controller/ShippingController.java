package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.payload.request.JSONStringInput;
import com.sep490.gshop.payload.request.shipment.ShipmentStatusRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.ShippingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(path = "/tracking-token")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Object> getTrackingToken(@RequestParam DeliveryCode deliveryCode) {
        log.info("getTrackingToken() ShippingController Start | deliveryCode: {}", deliveryCode.getName());
        String token = shippingService.getTrackingToken(deliveryCode);
        log.info("getTrackingToken() End | token: {}", token);
        return ResponseEntity.ok(token);
    }

    @GetMapping(path = "/shipping-token")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Object> getShippingToken(@RequestParam DeliveryCode deliveryCode) {
        log.info("getShippingToken() ShippingController Start | deliveryCode: {}", deliveryCode.getName());
        String token = shippingService.getShippingToken(deliveryCode);
        log.info("getShippingToken() End | token: {}", token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("webhook")
    public ResponseEntity<MessageResponse> webhook(@RequestBody ShipmentStatusRequest payload) {
        log.info("webhook() ShippingController Start | payload: {}", payload);
        MessageResponse response = shippingService.handleWebhook(payload);
        log.info("webhook() End | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<String> getShippingRate(@RequestBody JSONStringInput inputJson) {
        log.info("getShippingRate() ShippingController Start | deliveryCode: {}", inputJson);
        String rate = shippingService.getShippingRate(inputJson);
        log.info("getShippingRate() End");
        return ResponseEntity.ok(rate);
    }

    @PostMapping("/ship")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<String> createShipment(@RequestBody JSONStringInput inputJson) {
        log.info("createShipment() ShippingController Start | deliveryCode: {}", inputJson);
        String rate = shippingService.createShipment(inputJson);
        log.info("createShipment() End");
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/tracking")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<String> getTrackingInfo(@RequestParam String trackingNumber,
                                                  @RequestParam DeliveryCode deliveryCode) {
        log.info("getTrackingInfo() ShippingController Start | trackingNumber: {}", trackingNumber);
        String trackingInfo = shippingService.tracking(trackingNumber, deliveryCode);
        log.info("getTrackingInfo() End | trackingInfo: {}", trackingInfo);
        return ResponseEntity.ok(trackingInfo);
    }
}
