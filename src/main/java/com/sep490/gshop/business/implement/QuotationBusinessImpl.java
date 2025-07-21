package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.QuotationBusiness;
import com.sep490.gshop.entity.Quotation;
import com.sep490.gshop.repository.QuotationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class QuotationBusinessImpl extends BaseBusinessImpl<Quotation, QuotationRepository> implements QuotationBusiness {
    protected QuotationBusinessImpl(QuotationRepository repository) {
        super(repository);
    }

    @Override
    public Optional<Quotation> findBySubRequest(UUID subRequestId) {
        return repository.findBySubRequest_Id(subRequestId);
    }
}
