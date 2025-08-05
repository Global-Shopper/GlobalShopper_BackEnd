package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.request.AdminRequest;
import com.sep490.gshop.payload.request.AdminUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(URLConstant.ADMIN)
@RequiredArgsConstructor
@Log4j2
@Validated
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    @Operation(summary = "Tạo mới admin")
    public ResponseEntity<AdminDTO> createAdmin(@Valid @RequestBody AdminRequest request) {
        log.info("createAdmin() start | email: {}", request.getEmail());
        AdminDTO dto = adminService.createAdmin(request);
        log.info("createAdmin() end | id: {}", dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    @Operation(summary = "Cập nhật admin")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable UUID id, @Valid @RequestBody AdminUpdateRequest request) {
        log.info("updateAdmin() start | id: {}", id);
        AdminDTO dto = adminService.updateAdmin(id, request);
        log.info("updateAdmin() end | id: {}", dto.getId());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    @Operation(summary = "Xoá admin")
    public ResponseEntity<MessageResponse> deleteAdmin(@PathVariable UUID id) {
        log.info("deleteAdmin() start | id: {}", id);
        MessageResponse response = adminService.deleteAdmin(id);
        log.info("deleteAdmin() end | id: {}, success: {}", id, response.isSuccess());
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    @Operation(summary = "lấy profile của admin bằng id")
    public ResponseEntity<AdminDTO> getAdmin(@PathVariable UUID id) {
        log.info("getAdmin() start | id: {}", id);
        AdminDTO dto = adminService.getAdmin(id);
        log.info("getAdmin() end | id: {}", id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "lầy profile của admin hiện tại")
    public ResponseEntity<AdminDTO> getCurrentAdmin() {
        log.info("getCurrentAdmin() start");
        AdminDTO dto = adminService.getCurrentAdmin();
        log.info("getCurrentAdmin() end | id: {}", dto.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUSINESS_MANAGER')")
    @Operation(summary = "Lấy danh sách admin")

    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        log.info("getAllAdmins() start");
        List<AdminDTO> result = adminService.getAllAdmins();
        log.info("getAllAdmins() end | count: {}", result.size());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Upload avatar cho admin hiện tại")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminDTO> uploadAvatar(@RequestPart("file") MultipartFile file) {
        log.info("uploadAvatar() Controller Start | filename: {}", file.getOriginalFilename());

        AdminDTO updatedCustomer = adminService.uploadAvatar(file);

        log.info("uploadAvatar() Controller End | avatarUrl: {}", updatedCustomer.getAvatar());

        return ResponseEntity.ok(updatedCustomer);
    }
}