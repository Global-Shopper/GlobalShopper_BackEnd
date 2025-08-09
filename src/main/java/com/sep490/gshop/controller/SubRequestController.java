package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.SubUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.SubRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(URLConstant.SUB_REQUEST)
@Log4j2
@CrossOrigin("*")
public class SubRequestController {
    @Autowired
    private SubRequestService subRequestService;

    @DeleteMapping("/{subRequestId}/items/{itemId}")
    public ResponseEntity<MessageResponse> removeRequestItem(
            @PathVariable UUID subRequestId,
            @PathVariable UUID itemId) {
        log.info("[API] removeRequestItem - START | subRequestId: {}, itemId: {}", subRequestId, itemId);
        MessageResponse response = subRequestService.removeRequestItem(subRequestId, itemId);
        log.info("[API] removeRequestItem - END | subRequestId: {}, itemId: {}, message: {}",
                subRequestId, itemId, response.getMessage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{subRequestId}/items/{itemId}")
    public ResponseEntity<MessageResponse> addRequestItem(
            @PathVariable UUID subRequestId,
            @PathVariable UUID itemId) {
        log.info("[API] addRequestItem - START | subRequestId: {}, itemId: {}", subRequestId, itemId);
        MessageResponse response = subRequestService.addRequestItem(subRequestId, itemId);
        log.info("[API] addRequestItem - END | subRequestId: {}, itemId: {}, message: {}",
                subRequestId, itemId, response.getMessage());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{subRequestId}")
    public ResponseEntity<SubRequestDTO> updateSubRequest(
            @PathVariable UUID subRequestId,
            @RequestBody SubUpdateRequest subUpdateRequest) {
        log.info("[API] updateSubRequest - START | subRequestId: {}, requestBody: {}", subRequestId, subUpdateRequest);
        SubRequestDTO updatedSubRequest = subRequestService.updateSubRequest(subRequestId, subUpdateRequest);
        log.info("[API] updateSubRequest - END | subRequestId: {}, updatedSeller: {}, updatedPlatform: {}",
                subRequestId, updatedSubRequest.getSeller(), updatedSubRequest.getEcommercePlatform());
        return ResponseEntity.ok(updatedSubRequest);
    }
}
