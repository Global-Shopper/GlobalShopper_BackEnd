package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.FeedbackBusiness;
import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Feedback;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.request.FeedbackRequest;
import com.sep490.gshop.service.FeedbackService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@Log4j2
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackBusiness feedbackBusiness;
    private final ModelMapper modelMapper;
    private final OrderBusiness orderBusiness;
    @Autowired
    public FeedbackServiceImpl( FeedbackBusiness feedbackBusiness, ModelMapper modelMapper, OrderBusiness orderBusiness) {
        this.feedbackBusiness = feedbackBusiness;
        this.modelMapper = modelMapper;
        this.orderBusiness = orderBusiness;
    }

    @Override
    public FeedbackDTO createFeedback(FeedbackRequest request) {
        log.debug("createFeedback() FeedbackServiceImpl Start | request: {}", request);
        try {
            Feedback newFeedback = modelMapper.map(request, Feedback.class);
            Order order = orderBusiness.getById(UUID.fromString(request.getOrderId()))
                    .orElseThrow(() -> new AppException(404, "Order not found"));
            UUID userId = AuthUtils.getCurrentUserId();
            if (!order.getCustomer().getId().equals(userId)) {
                throw new AppException(403, "Bạn không có quyền đánh giá đơn hàng này");
            }
            if (!OrderStatus.DELIVERED.equals(order.getStatus())) {
                throw new AppException(400, "Chỉ có thể đánh giá đơn hàng đã giao");
            }
            newFeedback.setOrder(order);
            FeedbackDTO dto = modelMapper.map(feedbackBusiness.create(newFeedback), FeedbackDTO.class);
            log.debug("createFeedback() FeedbackServiceImpl End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createFeedback() FeedbackServiceImpl Exception | request: {}, message: {}", request, e.getMessage());
            throw e;
        }
    }

    @Override
    public FeedbackDTO updateFeedback(UUID id, FeedbackRequest request) {
        log.debug("updateFeedback() FeedbackServiceImpl Start | id: {}, request: {}", id, request);
        try {
            var foundEntity = feedbackBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Feedback is not found"));
            foundEntity.setComment(request.getComment());
            foundEntity.setRating(request.getRating());
            FeedbackDTO dto = modelMapper.map(feedbackBusiness.update(foundEntity), FeedbackDTO.class);
            log.debug("updateFeedback() FeedbackServiceImpl End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("updateFeedback() FeedbackServiceImpl Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteFeedback(UUID id) {
        log.debug("deleteFeedback() FeedbackServiceImpl Start | id: {}", id);
        try {
            boolean success = feedbackBusiness.delete(id);
            log.debug("deleteFeedback() FeedbackServiceImpl End | id: {}, result: {}", id, success);
            return success;
        } catch (Exception e) {
            log.error("deleteFeedback() FeedbackServiceImpl Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<FeedbackDTO> getAllFeedback() {
        log.debug("getAllFeedback() FeedbackServiceImpl Start");
        try {
            var entitysList = feedbackBusiness.getAll().stream()
                    .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
                    .toList();
            log.debug("getAllFeedback() FeedbackServiceImpl End | size: {}", entitysList.size());
            return entitysList;
        } catch (Exception e) {
            log.error("getAllFeedback() FeedbackServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public FeedbackDTO getFeedbackById(UUID id) {
        log.debug("getFeedbackById() FeedbackServiceImpl Start | id: {}", id);
        try {
            var entityFound = feedbackBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Feedback is not found"));
            FeedbackDTO dto = modelMapper.map(entityFound, FeedbackDTO.class);
            log.debug("getFeedbackById() FeedbackServiceImpl End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("getFeedbackById() FeedbackServiceImpl Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }
}
