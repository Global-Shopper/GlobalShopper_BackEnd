package com.sep490.gshop.controller;

import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.request.AdminRequest;
import com.sep490.gshop.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Log4j2
@Validated
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Tạo mới admin, gửi mật khẩu qua email")
    @PostMapping
    public ResponseEntity<AdminDTO> createAdmin(@Valid @RequestBody AdminRequest request) {
        log.debug("createAdmin() Start | request: {}", request);
        AdminDTO dto = adminService.createAdmin(request);
        log.debug("createAdmin() End | dto: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Xóa admin theo ID")
    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID adminId) {
        log.debug("deleteAdmin() Start | adminId: {}", adminId);
        adminService.deleteAdmin(adminId);
        log.debug("deleteAdmin() End | adminId: {}", adminId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy thông tin admin theo ID")
    @GetMapping("/{adminId}")
    public ResponseEntity<AdminDTO> getAdmin(@PathVariable UUID adminId) {
        log.debug("getAdmin() Start | adminId: {}", adminId);
        AdminDTO dto = adminService.getAdmin(adminId);
        log.debug("getAdmin() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy danh sách tất cả admin")
    @GetMapping
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        log.debug("getAllAdmins() Start");
        List<AdminDTO> list = adminService.getAllAdmins();
        log.debug("getAllAdmins() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }
}