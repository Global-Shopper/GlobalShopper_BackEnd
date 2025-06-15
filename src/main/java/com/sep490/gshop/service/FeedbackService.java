package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.request.FeedbackRequest;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    FeedbackDTO createFeedback(FeedbackRequest request);
    FeedbackDTO updateFeedback(UUID id, FeedbackRequest request);
    boolean deleteFeedback(UUID id);
    List<FeedbackDTO> getAllFeedback();
    FeedbackDTO getFeedbackById(UUID id);
}
