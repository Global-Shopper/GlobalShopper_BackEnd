package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.AdminBusiness;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.payload.dto.AdminDTO;
import org.thymeleaf.context.Context;
import com.sep490.gshop.payload.request.AdminRequest;
import com.sep490.gshop.payload.request.AdminUpdateRequest;
import com.sep490.gshop.payload.response.CloudinaryResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.AdminService;
import com.sep490.gshop.service.CloudinaryService;
import com.sep490.gshop.service.EmailService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@Validated
public class AdminServiceImpl implements AdminService {

    private final AdminBusiness adminBusiness;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private CloudinaryService cloudinaryService;

    @Autowired
    public AdminServiceImpl(AdminBusiness adminBusiness,
                            ModelMapper modelMapper,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService, CloudinaryService cloudinaryService) {
        this.adminBusiness = adminBusiness;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public AdminDTO createAdmin(@Valid AdminRequest adminRequest) {
        log.debug("createAdmin() Start | request: {}", adminRequest);
        try {
            Admin admin = modelMapper.map(adminRequest, Admin.class);

            String rawPassword = generateRandomPassword();

            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setRole(UserRole.ADMIN);
            admin.setEmailVerified(true);
            Admin createdAdmin = adminBusiness.create(admin);

            sendPasswordEmail(adminRequest.getEmail(), rawPassword);

            AdminDTO dto = modelMapper.map(createdAdmin, AdminDTO.class);
            log.debug("createAdmin() End | adminId: {}", createdAdmin.getId());
            return dto;
        } catch (Exception e) {
            log.error("createAdmin() Exception | message: {}", e.getMessage());
            throw new AppException(500, "Failed to create admin: " + e.getMessage());
        }
    }

    public AdminDTO updateAdmin(UUID id, @Valid AdminUpdateRequest adminUpdateReq) {
        log.debug("updateAdmin() Start | email: {}", adminUpdateReq.getEmail());
        try {
            Admin admin = adminBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Admin không tồn tại với email: " + adminUpdateReq.getEmail()));

            admin.setName(adminUpdateReq.getName());
            admin.setDateOfBirth(adminUpdateReq.getDateOfBirth());
            admin.setNation(adminUpdateReq.getNation());
            admin.setPhone(adminUpdateReq.getPhone());
            admin.setAddress(adminUpdateReq.getAddress());
            admin.setGender(adminUpdateReq.getGender());
            admin.setActive(adminUpdateReq.isActive());

            Admin saved = adminBusiness.update(admin);

            AdminDTO dto = modelMapper.map(saved, AdminDTO.class);
            log.debug("updateAdmin() End | adminId: {}", saved.getId());
            return dto;
        } catch (Exception e) {
            log.error("updateAdmin() Exception | message: {}", e.getMessage());
            throw new AppException(500, "Failed to update admin: " + e.getMessage());
        }
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void sendPasswordEmail(String email, String password) {
        String subject = "Your Admin Account Password";
        Context context = new Context();
        context.setVariable("password", password);
        context.setVariable("loginUrl", "http://103.211.201.144:3000/login");

        emailService.sendEmailTemplate(
                email,
                subject,
                passwordEncoder.encode(password),
                "admin-password-mail.html",
                context
        );
    }


    public MessageResponse deleteAdmin(UUID adminId) {
        log.debug("deleteAdmin() Start | id: {}", adminId);
        try {
            boolean deleted = adminBusiness.delete(adminId);
            if (deleted) {
                log.debug("deleteAdmin() End | id: {}", adminId);
                return new MessageResponse("Xóa admin thành công", true);
            } else {
                log.debug("deleteAdmin() Fail | id: {}", adminId);
                return new MessageResponse("Không thể xóa admin này", false);
            }
        } catch (Exception e) {
            log.error("deleteAdmin() Exception | message: {}", e.getMessage());
            throw new AppException(500, "Failed to delete admin: " + e.getMessage());
        }
    }

    public AdminDTO getAdmin(UUID adminId) {
        log.debug("getAdmin() Start | id: {}", adminId);
        try {
            Admin admin = adminBusiness.getById(adminId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy admin với id: " + adminId));
            AdminDTO dto = modelMapper.map(admin, AdminDTO.class);
            log.debug("getAdmin() End | id: {}", adminId);
            return dto;
        } catch (Exception e) {
            log.error("getAdmin() Exception | message: {}", e.getMessage());
            throw new AppException(500, "Failed to get admin: " + e.getMessage());
        }
    }
@Override
    public AdminDTO getCurrentAdmin() {
        log.debug("getAdmin() Start");
        try {
            UUID adminId = AuthUtils.getCurrentUserId();
            Admin admin = adminBusiness.getById(adminId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy admin với id: " + adminId));
            AdminDTO dto = modelMapper.map(admin, AdminDTO.class);
            log.debug("getAdmin() End | id: {}", adminId);
            return dto;
        } catch (Exception e) {
            log.error("getAdmin() Exception | message: {}", e.getMessage());
            throw new AppException(500, "Failed to get admin: " + e.getMessage());
        }
    }

    @Override
    public List<AdminDTO> getAllAdmins() {
        log.debug("getAllAdmins() Start");
        try {
            List<Admin> admins = adminBusiness.getAll();
            List<AdminDTO> result = admins.stream()
                    .map(a -> modelMapper.map(a, AdminDTO.class))
                    .toList();
            log.debug("getAllAdmins() End | count: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("getAllAdmins() Exception | message: {}", e.getMessage());
            throw new AppException(500, "Failed to get all admins: " + e.getMessage());
        }
    }
    @Override
    public AdminDTO uploadAvatar(MultipartFile multipartFile) {
        log.debug("uploadAvatar() Start | filename: {}", multipartFile.getOriginalFilename());
        try {
            UUID userId = AuthUtils.getCurrentUserId();
            var admin = adminBusiness.getById(userId).orElseThrow(EntityNotFoundException::new);
            FileUploadUtil.AssertAllowedExtension(multipartFile, FileUploadUtil.IMAGE_PATTERN);

            String fileName = FileUploadUtil.formatFileName(multipartFile.getOriginalFilename());

            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(multipartFile, fileName);

            admin.setAvatar(cloudinaryResponse.getResponseURL());

            var updatedUser = modelMapper.map(adminBusiness.update(admin), AdminDTO.class);
            log.debug("uploadAvatar() End | avatarUrl: {}", updatedUser.getAvatar());

            return updatedUser;
        }  catch (Exception e) {
            log.error("uploadAvatar() Unexpected Exception | message: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi upload avatar: " + e.getMessage());
        }
    }

    @Override
    public MessageResponse toggleAdminActiveStatus(UUID id) {
        try {
            log.debug("toggleAdminActiveStatus() AdminServiceImpl Start | id: {}", id);
            Admin admin = adminBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy admin với id: " + id));
            admin.setActive(!admin.isActive());
            Admin updatedAdmin = adminBusiness.update(admin);
            String status = updatedAdmin.isActive() ? "Kích hoạt" : "Vô hiệu hoá";
            log.debug("toggleAdminActiveStatus() AdminServiceImpl End | id: {}, status: {}", id, status);
            return new MessageResponse(status  +" admin thành công", true);
        } catch (Exception e) {
            log.error("toggleAdminActiveStatus() AdminServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }
}
