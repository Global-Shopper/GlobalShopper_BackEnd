package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.dto.TaxRateSnapshotDTO;
import com.sep490.gshop.payload.request.QuotationDetailBatchRequest;
import com.sep490.gshop.payload.request.QuotationDetailRequest;
import com.sep490.gshop.service.QuotationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuotationServiceImpl implements QuotationService {
    private QuotationBusiness quotationBusiness;
    private QuotationDetailBusiness quotationDetailBusiness;
    private SubRequestBusiness subRequestBusiness;
    private RequestItemBusiness requestItemBusiness;
    private TaxRateBusiness taxRateBusiness;
    private HsCodeBusiness hsCodeBusiness;
    @Autowired
    public QuotationServiceImpl(QuotationBusiness quotationBusiness, SubRequestBusiness subRequestBusiness, RequestItemBusiness requestItemBusiness,
                                TaxRateBusiness taxRateBusiness, HsCodeBusiness hsCodeBusiness, QuotationDetailBusiness quotationDetailBusiness) {
        this.quotationBusiness = quotationBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.taxRateBusiness = taxRateBusiness;
        this.hsCodeBusiness = hsCodeBusiness;
        this.quotationDetailBusiness = quotationDetailBusiness;
    }


    @Override
    @Transactional
    public List<QuotationDetailDTO> createOrUpdateQuotationDetails(QuotationDetailBatchRequest batchInput){
        List<QuotationDetailDTO> quotationDetailDTOS = new ArrayList<>();
        for(QuotationDetailRequest rq : batchInput.getItems()){
            QuotationDetailDTO quotationDetailDTO = createOrUpdateQuotationDetail(rq);
            quotationDetailDTOS.add(quotationDetailDTO);
        }
        return quotationDetailDTOS;
    }

    QuotationDetailDTO createOrUpdateQuotationDetail(QuotationDetailRequest dto){
        UUID subRequestId = UUID.fromString(dto.getSubRequestId());
        Quotation quotation = quotationBusiness.findBySubRequest(subRequestId)
                .orElseGet(() -> {
                    SubRequest sub = subRequestBusiness.getById(subRequestId)
                            .orElseThrow(() -> new RuntimeException("SubRequest not found"));
                    Quotation q = new Quotation();
                    q.setSubRequest(sub);
                    q.setAccepted(false);
                    quotationBusiness.create(q);
                    return q;
                });

        RequestItem item = requestItemBusiness.getById(UUID.fromString(dto.getRequestItemId()))
                .orElseThrow(() -> new RuntimeException("RequestItem not found: " + dto.getRequestItemId()));

        QuotationDetail detail = quotationDetailBusiness.findByQuotationAndRequestItem(quotation, item)
                .orElseGet(() -> {
                    QuotationDetail d = new QuotationDetail();
                    d.setQuotation(quotation);
                    d.setRequestItem(item);
                    return d;
                });

        detail.setBasePrice(dto.getBasePrice());
        detail.setServiceFee(dto.getServiceFee());
        detail.setShippingEstimate(dto.getShippingEstimate());
        detail.setNote(dto.getNote());

        HsCode hsCode = hsCodeBusiness.getById(dto.getHsCodeId())
                .orElseThrow(() -> new RuntimeException("HsCode not found: " + dto.getHsCodeId()));

        List<TaxRate> taxRates = taxRateBusiness.findTaxRateHsCodeAndRegion(hsCode, dto.getRegion());
        List<TaxRateSnapshot> snapshots = taxRates.stream().map(TaxRateSnapshot::new).toList();
        detail.setTaxRates(snapshots);

        quotationDetailBusiness.create(detail);

        return mapDetailDto(detail);
    }

    private QuotationDetailDTO mapDetailDto(QuotationDetail detail) {
        QuotationDetailDTO dto = new QuotationDetailDTO();
        dto.setId(detail.getId().toString());
        dto.setRequestItemId(detail.getRequestItem().getId().toString());
        dto.setBasePrice(detail.getBasePrice());
        dto.setServiceFee(detail.getServiceFee());
        dto.setShippingEstimate(detail.getShippingEstimate());
        dto.setTaxRates(detail.getTaxRates().stream()
                .map(snap -> new TaxRateSnapshotDTO(snap.getRegion(), snap.getTaxType(), snap.getRate()))
                .toList());
        dto.setNote(detail.getNote());
        return dto;

    }


    @Override
    public QuotationDTO getQuotationById(String quotationId) {
        return null;
    }
}
