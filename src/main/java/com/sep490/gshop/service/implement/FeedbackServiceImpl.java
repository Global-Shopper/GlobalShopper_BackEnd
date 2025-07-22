package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.FeedbackBusiness;
import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Feedback;
import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.FeedbackRequest;
import com.sep490.gshop.service.FeedbackService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@Log4j2
public class FeedbackServiceImpl implements FeedbackService {
    private FeedbackBusiness feedbackBusiness;
    private ModelMapper modelMapper;
    private OrderBusiness orderBusiness;
    @Autowired
    public FeedbackServiceImpl( FeedbackBusiness feedbackBusiness, ModelMapper modelMapper, OrderBusiness orderBusiness) {
        this.feedbackBusiness = feedbackBusiness;
        this.modelMapper = modelMapper;
        this.orderBusiness = orderBusiness;
    }

    @Override
    public FeedbackDTO createFeedback(FeedbackRequest request) {
        log.debug("createFeedback() Start | request: {}", request);
        try {
            var newFeedback = modelMapper.map(request, Feedback.class);
            newFeedback.setId(UUID.randomUUID());
            newFeedback.setOrder(orderBusiness.getById(UUID.fromString(request.getOrderId()))
                    .orElseThrow(() -> new AppException(404, "Order not found")));
            FeedbackDTO dto = modelMapper.map(feedbackBusiness.create(newFeedback), FeedbackDTO.class);
            log.debug("createFeedback() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createFeedback() Exception | request: {}, message: {}", request, e.getMessage());
            throw e;
        }
    }

    @Override
    public FeedbackDTO updateFeedback(UUID id, FeedbackRequest request) {
        log.debug("updateFeedback() Start | id: {}, request: {}", id, request);
        try {
            var foundEntity = feedbackBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Feedback is not found"));
            // Bổ sung cập nhật các trường từ request vào entity
            foundEntity.setComment(request.getComment());
            foundEntity.setRating(request.getRating());
            // ... các trường khác nếu có
            FeedbackDTO dto = modelMapper.map(feedbackBusiness.update(foundEntity), FeedbackDTO.class);
            log.debug("updateFeedback() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("updateFeedback() Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteFeedback(UUID id) {
        log.debug("deleteFeedback() Start | id: {}", id);
        try {
            boolean success = feedbackBusiness.delete(id);
            log.debug("deleteFeedback() End | id: {}, result: {}", id, success);
            return success;
        } catch (Exception e) {
            log.error("deleteFeedback() Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<FeedbackDTO> getAllFeedback() {
        log.debug("getAllFeedback() Start");
        try {
            var entitysList = feedbackBusiness.getAll().stream()
                    .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                    .toList();
            log.debug("getAllFeedback() End | size: {}", entitysList.size());
            return entitysList;
        } catch (Exception e) {
            log.error("getAllFeedback() Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public FeedbackDTO getFeedbackById(UUID id) {
        log.debug("getFeedbackById() Start | id: {}", id);
        try {
            var entityFound = feedbackBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Feedback is not found"));
            FeedbackDTO dto = modelMapper.map(entityFound, FeedbackDTO.class);
            log.debug("getFeedbackById() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("getFeedbackById() Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }
}
