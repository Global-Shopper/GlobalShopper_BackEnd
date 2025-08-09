package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.purchaserequest.OfflineRequest;
import com.sep490.gshop.payload.request.purchaserequest.OnlineRequest;
import com.sep490.gshop.payload.request.purchaserequest.SubRequestModel;
import com.sep490.gshop.payload.request.purchaserequest.UpdateRequestModel;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestModel;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import com.sep490.gshop.service.PurchaseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(URLConstant.PURCHASE_REQUEST)
@Log4j2
@CrossOrigin("*")
public class PurchaseRequestController {
    @Autowired
    private PurchaseRequestService purchaseRequestService;

    @Autowired
    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    @PostMapping("/online-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PurchaseRequestResponse<List<RequestItemDTO>>> createOnlinePurchaseRequest(@Valid @RequestBody OnlineRequest onlineRequest) {
        log.info("Creating purchase request: {}", onlineRequest);
        PurchaseRequestResponse<List<RequestItemDTO>> createdPurchaseRequest = purchaseRequestService.createOnlinePurchaseRequest(onlineRequest);
        log.info("Purchase request created successfully: {}", createdPurchaseRequest);
        return ResponseEntity.ok(createdPurchaseRequest);
    }

    @PostMapping("/offline-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PurchaseRequestResponse<SubRequestDTO>> createOfflinePurchaseRequest(@Valid @RequestBody OfflineRequest offlineRequest) {
        log.info("createOfflinePurchaseRequest() PurchaseRequestController start | model : {}", offlineRequest);
        PurchaseRequestResponse<SubRequestDTO> createdPurchaseRequest = purchaseRequestService.createOfflinePurchaseRequest(offlineRequest);
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

    @GetMapping()
    @PageableAsQueryParam
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Page<PurchaseRequestModel>> getPurchaseRequests(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) PurchaseRequestStatus status,
            @RequestParam(value = "type", defaultValue = "unassigned", required = false) String type) {
        log.info("getAllPurchaseRequests() PurchaseRequestController start | status: {}, type: {}", status, type);
        Page<PurchaseRequestModel> purchaseRequestDTO = purchaseRequestService.getPurchaseRequests(status,type, pageable);
        log.info("getAllPurchaseRequests() PurchaseRequestController end | purchaseRequestDTO: {}", purchaseRequestDTO);
        return ResponseEntity.ok(purchaseRequestDTO);
    }

    @PostMapping("/create-sub-request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> createSubRequest(@Valid @RequestBody SubRequestModel subRequestModel) {
        log.info("createSubRequest() PurchaseRequestController start | subRequestDTO: {}", subRequestModel);
        MessageResponse createdSubRequest = purchaseRequestService.createSubRequest(subRequestModel);
        log.info("createSubRequest() PurchaseRequestController end | createdSubRequest: {}", createdSubRequest);
        return ResponseEntity.ok(createdSubRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateRequest(@PathVariable("id") String id, @Valid @RequestBody UpdateRequestModel updateRequestModel) {
        log.info("updateRequest() PurchaseRequestController start | id: {}, updateRequestModel: {}", id, updateRequestModel);
        MessageResponse response = purchaseRequestService.updatePurchaseRequest(id, updateRequestModel);
        log.info("updateRequest() PurchaseRequestController end | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<PurchaseRequestModel> getPurchaseRequestById(@PathVariable("id") String id) {
        log.info("getPurchaseRequestById() PurchaseRequestController start | id: {}", id);
        if (id == null || id.trim().isEmpty()) {
            log.warn("Invalid id received: {}", id);
            return ResponseEntity.badRequest().build();
        }
        PurchaseRequestModel purchaseRequest = purchaseRequestService.getPurchaseRequestById(id);
        if (purchaseRequest == null) {
            log.warn("Purchase request not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("getPurchaseRequestById() PurchaseRequestController end | purchaseRequest: {}", purchaseRequest);
        return ResponseEntity.ok(purchaseRequest);
    }

    @PostMapping("/{id}/request-correction")
    @Operation(summary = "Yêu cầu cập nhật thông tin của request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> requestCorrection(
            @RequestParam UUID purchaseRequestId,
            @RequestBody String correctionNote) {
        log.info("requestCorrection() - Start | purchaseRequestId: {}, correctionNote: {}", purchaseRequestId, correctionNote);

        MessageResponse response = purchaseRequestService.requestCorrection(purchaseRequestId, correctionNote);

        log.info("requestCorrection() - End | purchaseRequestId: {}, success: {}", purchaseRequestId, response.isSuccess());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<UpdateRequestModel> getPurchaseRequestForEdit(@PathVariable("id") UUID purchaseRequestId) {
        log.info("getPurchaseRequestForEdit() - Start | purchaseRequestId: {}", purchaseRequestId);

        UpdateRequestModel updateRequest = purchaseRequestService.getPurchaseRequestForEdit(purchaseRequestId);

        log.info("getPurchaseRequestForEdit() - End | purchaseRequestId: {}", purchaseRequestId);
        return ResponseEntity.ok(updateRequest);
    }

}
