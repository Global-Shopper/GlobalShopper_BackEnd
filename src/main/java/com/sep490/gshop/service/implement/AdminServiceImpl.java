package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.AdminBusiness;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.payload.dto.AdminDTO;
import com.sep490.gshop.payload.request.AdminRequest;
import com.sep490.gshop.service.AdminService;
import com.sep490.gshop.service.EmailService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@Validated
public class AdminServiceImpl implements AdminService {

    private final AdminBusiness adminBusiness;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public AdminServiceImpl(AdminBusiness adminBusiness,
                            ModelMapper modelMapper,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService) {
        this.adminBusiness = adminBusiness;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public AdminDTO createAdmin(@Valid AdminRequest adminRequest) {
        log.debug("createAdmin() Start | request: {}", adminRequest);
        try {
            Admin admin = modelMapper.map(adminRequest, Admin.class);

            String rawPassword = generateRandomPassword();

            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setRole(UserRole.ADMIN);
            Admin createdAdmin = adminBusiness.create(admin);

            sendPasswordEmail(adminRequest.getEmail(), rawPassword);

            AdminDTO dto = modelMapper.map(createdAdmin, AdminDTO.class);
            log.debug("createAdmin() End | adminId: {}", createdAdmin.getId());
            return dto;
        } catch (Exception e) {
            log.error("createAdmin() Exception | message: {}", e.getMessage(), e);
            throw new AppException(500, "Failed to create admin: " + e.getMessage());
        }
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void sendPasswordEmail(String email, String password) {
        String subject = "Your Admin Account Password";
        String body = "Your temporary password is: " + password + "\n\n"
                + "Please change it after first login.";

        emailService.sendEmail(email, subject, body);
    }

    @Override
    public boolean deleteAdmin(UUID adminId) {
        return false;
    }

    @Override
    public AdminDTO getAdmin(UUID adminId) {
        return null;
    }

    @Override
    public List<AdminDTO> getAllAdmins() {
        return List.of();
    }
}
