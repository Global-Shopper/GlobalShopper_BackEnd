package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.FeedbackBusiness;
import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Feedback;
import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.request.FeedbackRequest;
import com.sep490.gshop.service.FeedbackService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
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
        var newFeedback = modelMapper.map(request, Feedback.class);
        newFeedback.setId(UUID.randomUUID());
        newFeedback.setOrder(orderBusiness.getById(UUID.fromString(request.getOrderId())).orElseThrow(() -> new AppException(404, "Order not found")));
        return modelMapper.map(feedbackBusiness.create(newFeedback), FeedbackDTO.class);
    }

    @Override
    public FeedbackDTO updateFeedback(UUID id, FeedbackRequest request) {
        var foundEntity = feedbackBusiness.getById(id).orElseThrow(() -> new AppException(404, "Feedback is not found"));
        return modelMapper.map(feedbackBusiness.update(foundEntity), FeedbackDTO.class);
        
    }

    @Override
    public FeedbackDTO deleteFeedback(FeedbackRequest request) {
        return null;
    }

    @Override
    public List<FeedbackDTO> getAllFeedback() {
        return List.of();
    }

    @Override
    public FeedbackDTO getFeedbackById(UUID id) {
        return null;
    }
}
