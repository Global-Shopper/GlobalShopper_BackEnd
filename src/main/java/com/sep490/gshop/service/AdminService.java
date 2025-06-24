package com.sep490.gshop.service;

import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.AdminRequest;
import com.sep490.gshop.payload.request.OrderRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AdminDTO createAdmin(@Valid AdminRequest admin);
    boolean deleteAdmin(UUID adminId);
    AdminDTO getAdmin(UUID adminId);
    List<AdminDTO> getAllAdmins();
}
