package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.UserDTO;
import com.sep490.gshop.payload.request.UserRequest;
import com.sep490.gshop.payload.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> getAllUsers();

    UserDTO createUser(UserRequest userRequest);

    UserDTO getUserById(String id);

    UserDTO updateUser(String id, UserRequest userRequest);

    boolean deleteUser(String id);

    MessageResponse toggleActiveStatus(UUID id);
}
