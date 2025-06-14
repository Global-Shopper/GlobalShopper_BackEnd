package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.FeedbackBusiness;
import com.sep490.gshop.entity.Feedback;
import com.sep490.gshop.repository.FeedbackRepository;
import org.springframework.stereotype.Component;

@Component
public class FeedbackBusinessImpl extends BaseBusinessImpl<Feedback, FeedbackRepository> implements FeedbackBusiness {
    protected FeedbackBusinessImpl(FeedbackRepository repository) {
        super(repository);
    }
}
