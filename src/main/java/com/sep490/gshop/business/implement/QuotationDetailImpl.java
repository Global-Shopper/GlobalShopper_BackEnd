package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.QuotationBusiness;
import com.sep490.gshop.business.QuotationDetailBusiness;
import com.sep490.gshop.entity.Quotation;
import com.sep490.gshop.entity.QuotationDetail;
import com.sep490.gshop.entity.RequestItem;
import com.sep490.gshop.repository.QuotationDetailRepository;
import com.sep490.gshop.repository.QuotationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class QuotationDetailImpl extends BaseBusinessImpl<QuotationDetail, QuotationDetailRepository> implements QuotationDetailBusiness {

    protected QuotationDetailImpl(QuotationDetailRepository repository) {
        super(repository);
    }

    @Override
    public Optional<QuotationDetail> findByQuotationAndRequestItem(Quotation quotation, RequestItem requestItem) {
        return repository.findByQuotationAndRequestItem(quotation, requestItem);
    }

    @Override
    public List<QuotationDetail> findByQuotation(Quotation quotation) {
        return repository.findByQuotation(quotation);
    }
}
