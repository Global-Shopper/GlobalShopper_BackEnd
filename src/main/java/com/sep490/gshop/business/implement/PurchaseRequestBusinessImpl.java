package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.PurchaseRequestBusiness;
import com.sep490.gshop.entity.PurchaseRequest;
import com.sep490.gshop.repository.PurchaseRequestRepository;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequestBusinessImpl extends BaseBusinessImpl<PurchaseRequest, PurchaseRequestRepository> implements PurchaseRequestBusiness {
    public PurchaseRequestBusinessImpl(PurchaseRequestRepository repository) {
        super(repository);
    }
}
