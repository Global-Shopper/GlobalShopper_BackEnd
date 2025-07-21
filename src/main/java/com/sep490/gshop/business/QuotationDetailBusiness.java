package com.sep490.gshop.business;

import com.sep490.gshop.entity.Quotation;
import com.sep490.gshop.entity.QuotationDetail;
import com.sep490.gshop.entity.RequestItem;

import java.util.List;
import java.util.Optional;

public interface QuotationDetailBusiness extends BaseBusiness<QuotationDetail>{
    Optional<QuotationDetail> findByQuotationAndRequestItem(Quotation quotation, RequestItem requestItem);
    List<QuotationDetail> findByQuotation(Quotation quotation);
}
