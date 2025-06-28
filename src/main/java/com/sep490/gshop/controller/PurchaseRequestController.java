package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.request.PurchaseRequestModel;
import com.sep490.gshop.service.PurchaseRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URLConstant.PURCHASE_REQUEST)
@Log4j2
@CrossOrigin("*")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @Autowired
    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    @PostMapping("/create")
    public ResponseEntity<PurchaseRequestDTO> createPurchaseRequest(@RequestBody PurchaseRequestModel purchaseRequestModel) {
        log.info("Creating purchase request: {}", purchaseRequestModel);
        PurchaseRequestDTO createdPurchaseRequest = purchaseRequestService.createPurchaseRequest(purchaseRequestModel);
        log.info("Purchase request created successfully: {}", createdPurchaseRequest);
        return ResponseEntity.ok(createdPurchaseRequest);
    }

}
