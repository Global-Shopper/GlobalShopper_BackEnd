package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.service.RequestItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping(URLConstant.REQUEST_ITEM)
@CrossOrigin("*")
@Log4j2
public class RequestItemController {
    @Autowired
    private RequestItemService requestItemService;
    @GetMapping("/{id}")
    public ResponseEntity<RequestItemDTO> getRequestItem(@PathVariable String id) {
        log.info("getRequestItem() - Start | id: {}", id);
        RequestItemDTO dto = requestItemService.getRequestItem(UUID.fromString(id));
        log.info("getRequestItem() - End | id: {}", id);
        return ResponseEntity.ok(dto);
    }

}
