package com.sep490.gshop.controller;

import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.request.FeedbackRequest;
import com.sep490.gshop.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    private FeedbackService feedbackService;
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackRequest request) {
        FeedbackDTO dto = feedbackService.createFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Cập nhật feedback
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(
            @PathVariable UUID id,
            @RequestBody FeedbackRequest request) {
        FeedbackDTO dto = feedbackService.updateFeedback(id, request);
        return ResponseEntity.ok(dto);
    }

    // Xóa feedback
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable UUID id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả feedback
    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedback() {
        List<FeedbackDTO> list = feedbackService.getAllFeedback();
        return ResponseEntity.ok(list);
    }

    // Lấy feedback theo id
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable UUID id) {
        FeedbackDTO dto = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(dto);
    }
}
