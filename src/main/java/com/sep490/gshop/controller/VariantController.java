package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.VariantDTO;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.VariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(URLConstant.VARIANT)
@RequiredArgsConstructor
@Slf4j
public class VariantController {
    private final VariantService variantService;
    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<VariantDTO> createVariant(@RequestParam String name) {
        log.info("createVariant() - Start | name: {}", name);
        VariantDTO dto = variantService.createVariant(name);
        log.info("createVariant() - End | id: {}", dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<List<VariantDTO>> getAllVariants() {
        log.info("getAllVariants() - Start");
        List<VariantDTO> variants = variantService.getAllVariants();
        log.info("getAllVariants() - End | count: {}", variants.size());
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<VariantDTO> getVariantById(@PathVariable UUID id) {
        log.info("getVariantById() - Start | id: {}", id);
        VariantDTO dto = variantService.getVariantById(id);
        log.info("getVariantById() - End | id: {}", id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<VariantDTO> updateVariantById(@PathVariable UUID id, @RequestParam String newName) {
        log.info("updateVariantById() - Start | id: {}, newName: {}", id, newName);
        VariantDTO dto = variantService.updateVariantById(id, newName);
        log.info("updateVariantById() - End | id: {}", id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<MessageResponse> deleteVariantById(@PathVariable UUID id) {
        log.info("deleteVariantById() - Start | id: {}", id);
        MessageResponse response = variantService.deleteVariantById(id);
        log.info("deleteVariantById() - End | id: {}, success: {}", id, response.isSuccess());
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
