package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.UserDTO;
import com.sep490.gshop.payload.request.UserRequest;
import com.sep490.gshop.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(URLConstant.USER)
@Log4j2
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        log.info("getUsers() UserController start");
        List<UserDTO> result = userService.getAllUsers();
        log.info("getUsers() UserController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("createUser() UserController start | userRequest: {}", userRequest);
        UserDTO result = userService.createUser(userRequest);
        log.info("createUser() UserController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        log.info("getUserById() UserController start | id: {}", id);
        UserDTO result = userService.getUserById(id);
        log.info("getUserById() UserController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequest userRequest) {
        log.info("updateUser() UserController start | id: {}, userRequest: {}", id, userRequest);
        UserDTO result = userService.updateUser(id, userRequest);
        log.info("updateUser() UserController end | {}", result);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("deleteUser() UserController start | id: {}", id);
        boolean check = userService.deleteUser(id);
        if (check) {
            log.info("deleteUser() UserController end | User with id {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } else {
            log.error("deleteUser() UserController end | User with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }
}
