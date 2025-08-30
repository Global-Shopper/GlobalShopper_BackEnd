package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.TaxRateSnapshotDTO;
import com.sep490.gshop.payload.request.TaxRateCreateAndUpdateRequest;
import com.sep490.gshop.payload.request.TaxRateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.TaxRateImportedResponse;
import com.sep490.gshop.service.TaxRateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(URLConstant.TAX_RATES)
@RequiredArgsConstructor
@Log4j2
@CrossOrigin("*")
public class TaxRateController  {
    private final TaxRateService taxRateService;

    @GetMapping("/by-hscode/{hsCode}")
    @Operation(summary = "Tìm tất cả thuế bằng hs code")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<List<TaxRateSnapshotDTO>> getTaxRatesByHsCode(@PathVariable String hsCode) {
        log.info("getTaxRatesByHsCode() - Start | hsCode: {}", hsCode);
        List<TaxRateSnapshotDTO> result = taxRateService.getTaxRatesByHsCode(hsCode);
        log.info("getTaxRatesByHsCode() - End | hsCode: {}, count: {}", hsCode, result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Tìm tax rate bằng id")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<TaxRateSnapshotDTO> getTaxRateById(@PathVariable String id) {
        log.info("getTaxRateById() - Start | id: {}", id);
        TaxRateSnapshotDTO dto = taxRateService.getTaxRateById(id);
        log.info("getTaxRateById() - End | id: {}", id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/by-hscode/{hsCode}")
    @Operation(summary = "Tạo mới tax rate (yêu cầu phải có hsCode)")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<TaxRateSnapshotDTO> createTaxRate(
            @Valid @RequestBody TaxRateCreateAndUpdateRequest req
    ) {
        log.info("createTaxRate() - Start | hsCode: {}, req: {}", req.getHsCode(), req);
        TaxRateSnapshotDTO dto = taxRateService.createTaxRate(req);
        log.info("createTaxRate() - End | hsCode: {}, createdId: {}", req.getHsCode(), dto.getTaxName());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "cập nhật thuế bằng id")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<TaxRateSnapshotDTO> updateTaxRate(
            @PathVariable String id,
            @Valid @RequestBody TaxRateCreateAndUpdateRequest req
    ) {
        log.info("updateTaxRate() - Start | id: {}, req: {}", id, req);
        TaxRateSnapshotDTO dto = taxRateService.updateTaxRate(id, req);
        log.info("updateTaxRate() - End | id: {}", id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá thuế")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    public ResponseEntity<MessageResponse> deleteTaxRate(@PathVariable String id) {
        log.info("deleteTaxRate() - Start | id: {}", id);
        MessageResponse result = taxRateService.deleteTaxRate(id);
        log.info("deleteTaxRate() - End | id: {}", id + ", isSuccess=" + result.isSuccess());
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/import-by-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> importTaxRates(@RequestPart("file") MultipartFile file) {
        log.info("[START] Import Tax Rates API called. File: {}", file.getOriginalFilename());

        long start = System.currentTimeMillis();
        MessageResponse response = taxRateService.importTaxRatesCSV(file);
        long end = System.currentTimeMillis();

        log.info("[END] Import Tax Rates API finished. Success: {}, Duration: {} ms",
                response.isSuccess(), (end - start));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/import-by-list")
    public ResponseEntity<TaxRateImportedResponse> importTaxRatesNewPhase(@Valid @RequestBody List<TaxRateRequest> requests) {
        log.info("Start importTaxRates | totalRequests={}", requests.size());

        TaxRateImportedResponse response = taxRateService.importTaxRatesNewPhaseCSV(requests);

        log.info("End importTaxRates | Inserted={} | Updated={} | Duplicated={}",
                response.getTaxRateImported(),
                response.getTaxRateUpdated(),
                response.getTaxRateDuplicated());

        return ResponseEntity.ok(response);
    }
}
