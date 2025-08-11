package com.sep490.gshop.business;

import com.sep490.gshop.entity.Feedback;

import java.util.UUID;

public interface FeedbackBusiness extends BaseBusiness<Feedback>{
    Feedback getByOrderId(UUID orderId);
}
