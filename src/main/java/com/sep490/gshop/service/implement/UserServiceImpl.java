package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.AdminBusiness;
import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.payload.dto.UserDTO;
import com.sep490.gshop.payload.request.UserRequest;
import com.sep490.gshop.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserBusiness userBusiness;
    private final AdminBusiness adminBusiness;
    private final CustomerBusiness customerBusiness;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserBusiness userBusiness, AdminBusiness adminBusiness, CustomerBusiness customerBusiness, ModelMapper modelMapper) {
        this.userBusiness = userBusiness;
        this.adminBusiness = adminBusiness;
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        try {
            log.info("getAllUsers() UserServiceImpl start");
            List<UserDTO> users = userBusiness.getAll().stream()
                    .map(user -> modelMapper.map(user, UserDTO.class))
                    .toList();
            log.info("getAllUsers() UserServiceImpl end | Users size: {}", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error in getAllUsers() UserServiceImpl: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public UserDTO createUser(UserRequest userRequest) {
        try {
            log.info("createUser() UserServiceImpl start | userRequest: {}", userRequest);
            User user;


            switch (userRequest.getRole()) {
                case CUSTOMER:
                    user = modelMapper.map(userRequest, Customer.class);
                    break;
                case ADMIN:
                    user = modelMapper.map(userRequest, Admin.class);
                    break;
                default:
                    user = modelMapper.map(userRequest, User.class);
            }
            User savedUser = userBusiness.create(user);
            UserDTO createdUser = modelMapper.map(savedUser, UserDTO.class);
            log.info("createUser() UserServiceImpl end | Created User: {}", createdUser);
            return createdUser;
        } catch (Exception e) {
            log.error("Error in createUser() UserServiceImpl: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDTO getUserById(String id) {
        try {
            log.info("getUserById() UserServiceImpl start | id: {}", id);
            User user = userBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng với id: " + id));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            log.info("getUserById() UserServiceImpl end | User: {}", userDTO);
            return userDTO;
        } catch (Exception e) {
            log.error("Error in getUserById() UserServiceImpl: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDTO updateUser(String id, UserRequest userRequest) {
        try {
            log.info("updateUser() UserServiceImpl start | id: {}, userRequest: {}", id, userRequest);
            User user = userBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng với id: " + id));

            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPhone(userRequest.getPhone());
            user.setAddress(userRequest.getAddress());
            user.setAvatar(userRequest.getAvatar());
            user.setRole(userRequest.getRole());
            user.setActive(userRequest.isActive());
            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                user.setPassword(userRequest.getPassword());
            }
            User updatedUser = userBusiness.update(user);
            UserDTO updatedUserDTO = modelMapper.map(updatedUser, UserDTO.class);
            log.info("updateUser() UserServiceImpl end | Updated User: {}", updatedUserDTO);
            return updatedUserDTO;
        } catch (Exception e) {
            log.error("Error in updateUser() UserServiceImpl: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteUser(String id) {
    try {
            log.info("deleteUser() UserServiceImpl start | id: {}", id);
            UUID userId = UUID.fromString(id);
            User user = userBusiness.getById(userId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng với id: " + id));
            userBusiness.delete(user.getId());
            log.info("deleteUser() UserServiceImpl end | User with id {} deleted successfully", id);
            return true;
        } catch (Exception e) {
            log.error("Error in deleteUser() UserServiceImpl: {}", e.getMessage(), e);
            throw e;
        }
    }
}
