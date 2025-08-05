package com.sep490.gshop.service;

import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.AdminRequest;
import com.sep490.gshop.payload.request.AdminUpdateRequest;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AdminDTO createAdmin(@Valid AdminRequest admin);
    AdminDTO updateAdmin(UUID id, @Valid AdminUpdateRequest admin);
    MessageResponse deleteAdmin(UUID adminId);
    AdminDTO getAdmin(UUID adminId);
    List<AdminDTO> getAllAdmins();
    AdminDTO getCurrentAdmin();
    AdminDTO uploadAvatar(MultipartFile multipartFile);
}
