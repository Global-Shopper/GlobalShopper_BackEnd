package com.sep490.gshop.controller;

import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.service.HsCodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hscode")
@CrossOrigin("*")
public class HsCodeController {

    private final HsCodeService hsCodeService;

    public HsCodeController(HsCodeService hsCodeService) {
        this.hsCodeService = hsCodeService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HsCodeDTO>> search(
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Page<HsCodeDTO> result = hsCodeService.findAll(description, page, size, direction);
        return ResponseEntity.ok(result);
    }


    // Các API khác nếu có ...
}
