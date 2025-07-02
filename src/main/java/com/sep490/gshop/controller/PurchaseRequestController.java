package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.entity.SubRequest;
import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.PurchaseRequestModel;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import com.sep490.gshop.service.PurchaseRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/online-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PurchaseRequestResponse<List<RequestItemDTO>>> createOnlinePurchaseRequest(@RequestBody PurchaseRequestModel purchaseRequestModel) {
        log.info("Creating purchase request: {}", purchaseRequestModel);
        PurchaseRequestResponse<List<RequestItemDTO>> createdPurchaseRequest = purchaseRequestService.createOnlinePurchaseRequest(purchaseRequestModel);
        log.info("Purchase request created successfully: {}", createdPurchaseRequest);
        return ResponseEntity.ok(createdPurchaseRequest);
    }

    @PostMapping("/offline-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PurchaseRequestResponse<SubRequestDTO>> createOfflinePurchaseRequest(@RequestBody PurchaseRequestModel purchaseRequestModel) {
        log.info("createOfflinePurchaseRequest() PurchaseRequestController start | model : {}", purchaseRequestModel);
        PurchaseRequestResponse<SubRequestDTO> createdPurchaseRequest = purchaseRequestService.createOfflinePurchaseRequest(purchaseRequestModel);
        log.info("createOfflinePurchaseRequest() PurchaseRequestController end | createdPurchaseRequest : {}", createdPurchaseRequest);
        return ResponseEntity.ok(createdPurchaseRequest);
    }

    @PatchMapping("/checking/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> checkPurchaseRequest(@PathVariable("id") String id) {
        log.info("checkPurchaseRequest() PurchaseRequestController start | id : {}", id);
        MessageResponse response = purchaseRequestService.checkPurchaseRequest(id);
        log.info("checkPurchaseRequest() PurchaseRequestController end");
        return ResponseEntity.ok(response);
    }

}
