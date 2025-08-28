package com.sep490.gshop.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.request.FCMTokenRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.FCMService;
import com.sep490.gshop.service.implement.SendNotiService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(URLConstant.FCM)
@Log4j2
@CrossOrigin("*")
public class FCMController {
    private final FCMService fcmService;
    private final SendNotiService sendNotiService;

    @Autowired
    public FCMController(FCMService fcmService, SendNotiService sendNotiService) {
        this.fcmService = fcmService;
        this.sendNotiService = sendNotiService;
    }

    @PostMapping("/save-token")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<MessageResponse> saveToken(@RequestBody FCMTokenRequest request) {
        log.info("saveToken() FCMController Start | request: {}", request);
        MessageResponse response = fcmService.saveToken(request);
        log.info("saveToken() End | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete-token")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<MessageResponse> deleteToken(@RequestBody FCMTokenRequest request) {
        log.info("deleteToken() FCMController Start | request: {}", request);
        MessageResponse response = fcmService.deleteToken(request);
        log.info("deleteToken() End | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() throws FirebaseMessagingException {
        log.info("test() FCMController Start");
        UUID uuid = AuthUtils.getCurrentUserId();
        sendNotiService.sendNotiToUser(uuid,"Test Title", "Test Body");
        log.info("test() FCMController End");
        return ResponseEntity.ok("Test successful");
    }
}
