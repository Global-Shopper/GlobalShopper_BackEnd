package com.sep490.gshop.controller;

import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.RefundTicketRequest;
import com.sep490.gshop.service.RefundTicketService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/refund-tickets")
public class RefundTicketController {
    private RefundTicketService refundTicketService;
    @Autowired
    public RefundTicketController(RefundTicketService refundTicketService) {
        this.refundTicketService = refundTicketService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "find refund ticket with id")
    public ResponseEntity<RefundTicketDTO> getRefundTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(refundTicketService.getRefundTicketById(id));
    }

    @PostMapping
    @Operation(summary = "create new refund ticket")
    public ResponseEntity<RefundTicketDTO> createRefundTicket(@RequestBody RefundTicketRequest refundTicketRequest) {
        return ResponseEntity.ok(refundTicketService.createNewRefundTicket(refundTicketRequest));
    }

    @GetMapping
    @Operation(summary = "get all refund tickets")
    public ResponseEntity<List<RefundTicketDTO>> getAllRefundTickets() {
        return ResponseEntity.ok(refundTicketService.getAllRefundTickets());
    }

    @PutMapping("/{id}")
    @Operation(summary = "update specific refund ticket")
    public ResponseEntity<RefundTicketDTO> updateRefundTicket(
            @PathVariable UUID id,
            @RequestBody RefundTicketRequest request) {
        return ResponseEntity.ok(refundTicketService.updateRefundTicket(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove specific refund ticket")
    public ResponseEntity<Void> deleteRefundTicket(@PathVariable UUID id) {
        refundTicketService.deleteRefundTicket(id);
        return ResponseEntity.noContent().build();
    }

}
