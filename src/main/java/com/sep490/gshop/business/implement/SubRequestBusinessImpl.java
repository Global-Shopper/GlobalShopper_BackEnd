package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.SubRequestBusiness;
import com.sep490.gshop.entity.SubRequest;
import com.sep490.gshop.repository.SubRequestRepository;
import org.springframework.stereotype.Component;

@Component
public class SubRequestBusinessImpl extends BaseBusinessImpl<SubRequest, SubRequestRepository> implements SubRequestBusiness {
    protected SubRequestBusinessImpl(SubRequestRepository repository) {
        super(repository);
    }
}
