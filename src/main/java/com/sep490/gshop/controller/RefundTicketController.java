package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.RefundTicketRequest;
import com.sep490.gshop.service.RefundTicketService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping(URLConstant.REFUND_TICKET)
public class RefundTicketController {
    private RefundTicketService refundTicketService;
    @Autowired
    public RefundTicketController(RefundTicketService refundTicketService) {
        this.refundTicketService = refundTicketService;
    }
    @GetMapping("/{id}")
    @Operation(summary = "find refund ticket with id")
    public ResponseEntity<RefundTicketDTO> getRefundTicketById(@PathVariable UUID id) {
        log.info("getRefundTicketById() Start | id: {}", id);
        RefundTicketDTO dto = refundTicketService.getRefundTicketById(id);
        log.info("getRefundTicketById() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "create new refund ticket")
    public ResponseEntity<RefundTicketDTO> createRefundTicket(@RequestBody RefundTicketRequest refundTicketRequest) {
        log.info("createRefundTicket() RefundTicketController Start | request: {}", refundTicketRequest);
        RefundTicketDTO dto = refundTicketService.createNewRefundTicket(refundTicketRequest);
        log.info("createRefundTicket() RefundTicketController End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "get all refund tickets")
    @PageableAsQueryParam
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Page<RefundTicketDTO>> getAllRefundTickets(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) RefundStatus status) {
        log.info("getAllRefundTickets() RefundTicketController Start");
        Page<RefundTicketDTO> list = refundTicketService.getAllRefundTickets(pageable, status);
        log.info("getAllRefundTickets() RefundTicketController End | size: {}", list.getSize());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update specific refund ticket")
    public ResponseEntity<RefundTicketDTO> updateRefundTicket(
            @PathVariable UUID id,
            @RequestBody RefundTicketRequest request) {
        log.info("updateRefundTicket() RefundTicketController Start | id: {}, request: {}", id, request);
        RefundTicketDTO dto = refundTicketService.updateRefundTicket(id, request);
        log.info("updateRefundTicket() RefundTicketController End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove specific refund ticket")
    public ResponseEntity<Void> deleteRefundTicket(@PathVariable UUID id) {
        log.info("deleteRefundTicket() RefundTicketController Start | id: {}", id);
        refundTicketService.deleteRefundTicket(id);
        log.info("deleteRefundTicket() RefundTicketController End | id: {}", id);
        return ResponseEntity.noContent().build();
    }


}
