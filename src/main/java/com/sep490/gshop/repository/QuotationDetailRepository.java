package com.sep490.gshop.repository;

import com.sep490.gshop.entity.Quotation;
import com.sep490.gshop.entity.QuotationDetail;
import com.sep490.gshop.entity.RequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuotationDetailRepository extends JpaRepository<QuotationDetail, UUID> {
    Optional<QuotationDetail> findByQuotationAndRequestItem(Quotation quotation, RequestItem requestItem);
    List<QuotationDetail> findByQuotation(Quotation quotation);
}
