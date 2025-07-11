package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.request.FeedbackRequest;
import com.sep490.gshop.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(URLConstant.FEEDBACK)
@Slf4j
@CrossOrigin("*")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackRequest request) {
        log.info("createFeedback() Start | request: {}", request);
        FeedbackDTO dto = feedbackService.createFeedback(request);
        log.info("createFeedback() End | dto: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(
            @PathVariable UUID id,
            @RequestBody FeedbackRequest request) {
        log.info("updateFeedback() Start | id: {}, request: {}", id, request);
        FeedbackDTO dto = feedbackService.updateFeedback(id, request);
        log.info("updateFeedback() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable UUID id) {
        log.info("deleteFeedback() Start | id: {}", id);
        feedbackService.deleteFeedback(id);
        log.info("deleteFeedback() End | id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedback() {
        log.info("getAllFeedback() Start");
        List<FeedbackDTO> list = feedbackService.getAllFeedback();
        log.info("getAllFeedback() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable UUID id) {
        log.info("getFeedbackById() Start | id: {}", id);
        FeedbackDTO dto = feedbackService.getFeedbackById(id);
        log.info("getFeedbackById() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }
}
