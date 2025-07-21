package com.sep490.gshop.business;

import com.sep490.gshop.entity.Quotation;

import java.util.Optional;
import java.util.UUID;

public interface QuotationBusiness extends BaseBusiness<Quotation>{
    Optional<Quotation> findBySubRequest(UUID subRequestId);
}
