package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import com.sep490.gshop.payload.request.ShippingAddressRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.ShippingAddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping(URLConstant.SHIPPING_ADDRESS)
@CrossOrigin("*")
public class ShippingAddressController {

    private final ShippingAddressService shippingAddressService;

    @Autowired
    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    @Operation(summary = "Create new shipping address for current user")
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ShippingAddressDTO> createShippingAddress(@Valid @RequestBody ShippingAddressRequest request) {
        log.info("createShippingAddress() Start | request: {}", request);
        ShippingAddressDTO dto = shippingAddressService.createShippingAddress(request);
        log.info("createShippingAddress() End | dto: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Update shipping address by ID for current user")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ShippingAddressDTO> updateShippingAddress(
            @PathVariable UUID id,
            @Valid @RequestBody ShippingAddressRequest request) {
        log.info("updateShippingAddress() Start | id: {}, request: {}", id, request);
        ShippingAddressDTO dto = shippingAddressService.updateDefaultShippingAddress(request, id);
        log.info("updateShippingAddress() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get shipping address by ID for current user")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ShippingAddressDTO> getShippingAddress(@PathVariable UUID id) {
        log.info("getShippingAddress() Start | id: {}", id);
        ShippingAddressDTO dto = shippingAddressService.getShippingAddress(id);
        log.info("getShippingAddress() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all shipping addresses of current user")
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ShippingAddressDTO>> getShippingAddresses() {
        log.info("getShippingAddresses() getShippingAddresses Start");
        List<ShippingAddressDTO> list = shippingAddressService.getShippingAddressesByCurrentUser();
        log.info("getShippingAddresses() getShippingAddresses End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/default/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public MessageResponse updateShippingAddress(@PathVariable UUID id){
        log.info("updateShippingAddress() updateShippingAddress Start | id: {}", id);
        var shipping = shippingAddressService.updateDefaultShippingAddress(id);
        log.info("updateShippingAddress() updateShippingAddress End | dto: {}", shipping);
        return MessageResponse.builder().message("Update thành công").isSuccess(shipping).build();
    }

    @Operation(summary = "Delete shipping address by ID for current user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public MessageResponse deleteShippingAddress(@PathVariable UUID id) {
        log.info("deleteShippingAddress() Start | id: {}", id);
        var shipping = shippingAddressService.deleteShippingAddress(id);
        log.info("deleteShippingAddress() End | id: {}", id);
        return MessageResponse.builder().message("Xoá địa chỉ thành công").isSuccess(shipping).build();
    }
}
