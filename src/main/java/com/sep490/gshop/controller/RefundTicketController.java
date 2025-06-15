package com.sep490.gshop.controller;

import com.sep490.gshop.common.URLConstant;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.RefundTicketRequest;
import com.sep490.gshop.service.RefundTicketService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping(URLConstant.REFUNDTICKET)
public class RefundTicketController {
    private RefundTicketService refundTicketService;
    @Autowired
    public RefundTicketController(RefundTicketService refundTicketService) {
        this.refundTicketService = refundTicketService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "find refund ticket with id")
    public ResponseEntity<RefundTicketDTO> getRefundTicketById(@PathVariable UUID id) {
        log.debug("getRefundTicketById() Start | id: {}", id);
        RefundTicketDTO dto = refundTicketService.getRefundTicketById(id);
        log.debug("getRefundTicketById() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "create new refund ticket")
    public ResponseEntity<RefundTicketDTO> createRefundTicket(@RequestBody RefundTicketRequest refundTicketRequest) {
        log.debug("createRefundTicket() Start | request: {}", refundTicketRequest);
        RefundTicketDTO dto = refundTicketService.createNewRefundTicket(refundTicketRequest);
        log.debug("createRefundTicket() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "get all refund tickets")
    public ResponseEntity<List<RefundTicketDTO>> getAllRefundTickets() {
        log.debug("getAllRefundTickets() Start");
        List<RefundTicketDTO> list = refundTicketService.getAllRefundTickets();
        log.debug("getAllRefundTickets() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update specific refund ticket")
    public ResponseEntity<RefundTicketDTO> updateRefundTicket(
            @PathVariable UUID id,
            @RequestBody RefundTicketRequest request) {
        log.debug("updateRefundTicket() Start | id: {}, request: {}", id, request);
        RefundTicketDTO dto = refundTicketService.updateRefundTicket(id, request);
        log.debug("updateRefundTicket() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove specific refund ticket")
    public ResponseEntity<Void> deleteRefundTicket(@PathVariable UUID id) {
        log.debug("deleteRefundTicket() Start | id: {}", id);
        refundTicketService.deleteRefundTicket(id);
        log.debug("deleteRefundTicket() End | id: {}", id);
        return ResponseEntity.noContent().build();
    }


}
