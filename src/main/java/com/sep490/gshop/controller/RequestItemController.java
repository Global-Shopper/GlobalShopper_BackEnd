package com.sep490.gshop.controller;

import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.service.RequestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping("/api/request-items")

public class RequestItemController {
    @Autowired
    private RequestItemService requestItemService;
    @GetMapping("/{id}")
    public ResponseEntity<RequestItemDTO> getRequestItem(@PathVariable String id) {
        RequestItemDTO dto = requestItemService.getRequestItem(UUID.fromString(id));
        return ResponseEntity.ok(dto);
    }
}
