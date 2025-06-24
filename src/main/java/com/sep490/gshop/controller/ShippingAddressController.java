package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import com.sep490.gshop.payload.request.ShippingAddressRequest;
import com.sep490.gshop.service.ShippingAddressService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping(URLConstant.SHIPPINGADDRESS)
public class ShippingAddressController {
    private ShippingAddressService shippingAddressService;
    @Autowired
    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    @Operation(summary = "Create new shipping address")
    @PostMapping
    public ResponseEntity<ShippingAddressDTO> createShippingAddress(@RequestBody ShippingAddressRequest request) {
        log.debug("createShippingAddress() Start | request: {}", request);
        ShippingAddressDTO dto = shippingAddressService.createShippingAddress(request);
        log.debug("createShippingAddress() End | dto: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Update shipping address")
    @PutMapping("/{id}")
    public ResponseEntity<ShippingAddressDTO> updateShippingAddress(
            @PathVariable UUID id,
            @RequestBody ShippingAddressRequest request) {
        log.debug("updateShippingAddress() Start | id: {}, request: {}", id, request);
        ShippingAddressDTO dto = shippingAddressService.updateShippingAddress(request, id);
        log.debug("updateShippingAddress() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get shipping address by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ShippingAddressDTO> getShippingAddress(@PathVariable UUID id) {
        log.debug("getShippingAddress() Start | id: {}", id);
        ShippingAddressDTO dto = shippingAddressService.getShippingAddress(id);
        log.debug("getShippingAddress() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all shipping address")
    @GetMapping
    public ResponseEntity<List<ShippingAddressDTO>> getShippingAddresses() {
        log.debug("getShippingAddresses() Start");
        List<ShippingAddressDTO> list = shippingAddressService.getShippingAddresses();
        log.debug("getShippingAddresses() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Delete shipping address")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable UUID id) {
        log.debug("deleteShippingAddress() Start | id: {}", id);
        shippingAddressService.deleteShippingAddress(id);
        log.debug("deleteShippingAddress() End | id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
