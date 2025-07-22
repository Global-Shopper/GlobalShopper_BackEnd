package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.request.QuotationDetailRequest;
import com.sep490.gshop.payload.request.QuotationInputRequest;
import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.ExchangeRateService;
import com.sep490.gshop.service.QuotationService;
import com.sep490.gshop.service.TaxRateService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
@Log4j2
@Service
public class QuotationServiceImpl implements QuotationService {
    private QuotationBusiness quotationBusiness;
    private QuotationDetailBusiness quotationDetailBusiness;
    private SubRequestBusiness subRequestBusiness;
    private RequestItemBusiness requestItemBusiness;
    private TaxRateBusiness taxRateBusiness;
    private HsCodeBusiness hsCodeBusiness;
    private ModelMapper modelMapper;
    private TaxRateService taxRateService;
    private ExchangeRateService exchangeRateService;
    @Autowired
    public QuotationServiceImpl(QuotationBusiness quotationBusiness, SubRequestBusiness subRequestBusiness, RequestItemBusiness requestItemBusiness,
                                TaxRateBusiness taxRateBusiness, HsCodeBusiness hsCodeBusiness, QuotationDetailBusiness quotationDetailBusiness, ModelMapper modelMapper
    , TaxRateService taxRateService, ExchangeRateService exchangeRateService) {
        this.quotationBusiness = quotationBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.taxRateBusiness = taxRateBusiness;
        this.hsCodeBusiness = hsCodeBusiness;
        this.quotationDetailBusiness = quotationDetailBusiness;
        this.modelMapper = modelMapper;
        this.taxRateService = taxRateService;
        this.exchangeRateService = exchangeRateService;
    }


    @Override
    @Transactional
    public QuotationDTO createOrUpdateQuotation(QuotationInputRequest input) {
        log.info("createOrUpdateQuotation() - Start | subRequestId: {}", input.getSubRequestId());
        try {
            UUID subRequestId = UUID.fromString(input.getSubRequestId());
            Quotation quotation = quotationBusiness.findBySubRequest(subRequestId)
                    .orElseGet(() -> {
                        SubRequest sub = subRequestBusiness.getById(subRequestId)
                                .orElseThrow(() -> AppException.builder()
                                        .message("Không tìm thấy sub request")
                                        .code(404)
                                        .build());
                        Quotation q = new Quotation();
                        q.setSubRequest(sub);
                        q.setAccepted(false);
                        q.setNote(input.getNote());
                        quotationBusiness.create(q);
                        return q;
                    });

            quotation.setNote(input.getNote());

            List<QuotationDetailDTO> detailDTOs = new ArrayList<>();
            for (QuotationDetailRequest detailReq : input.getDetails()) {
                RequestItem item = requestItemBusiness.getById(UUID.fromString(detailReq.getRequestItemId()))
                        .orElseThrow(() -> AppException.builder()
                                .message("RequestItem không tìm thấy: " + detailReq.getRequestItemId())
                                .code(404)
                                .build());

                QuotationDetail detail = quotationDetailBusiness.findByQuotationAndRequestItem(quotation, item)
                        .orElseGet(() -> {
                            QuotationDetail d = new QuotationDetail();
                            d.setQuotation(quotation);
                            d.setRequestItem(item);
                            return d;
                        });

                detail.setBasePrice(detailReq.getBasePrice());
                detail.setServiceFee(detailReq.getServiceFee());
                detail.setShippingEstimate(detailReq.getShippingEstimate());
                detail.setNote(detailReq.getNote());

                HsCode hsCode = hsCodeBusiness.getById(detailReq.getHsCodeId())
                        .orElseThrow(() -> AppException.builder()
                                .message("HsCode không tìm thấy: " + detailReq.getHsCodeId())
                                .code(404)
                                .build());

                List<TaxRate> taxRates = taxRateBusiness.findTaxRateHsCodeAndRegion(hsCode, detailReq.getRegion());
                List<TaxRateSnapshot> snapshots = taxRates.stream()
                        .map(m -> modelMapper.map(m, TaxRateSnapshot.class))
                        .toList();
                detail.setTaxRates(snapshots);

                TaxCalculationResult taxResult = taxRateService.calculateTaxes(detailReq.getBasePrice(), taxRates);

                quotationDetailBusiness.create(detail);

                QuotationDetailDTO detailDTO = modelMapper.map(detail, QuotationDetailDTO.class);
                detailDTO.setTaxAmounts(taxResult.getTaxAmounts());
                detailDTO.setRequestItemId(detailReq.getRequestItemId());
                detailDTOs.add(detailDTO);
            }

            QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);
            dto.setDetails(detailDTOs);
            dto.setSubRequestId(input.getSubRequestId());

            String type = input.getType() != null ? input.getType().toUpperCase(Locale.ROOT) : "USD";

            double total = calculateTotalPrice(detailDTOs);

            double totalPriceEstimate = total;
            if (!"VND".equalsIgnoreCase(type)) {
                CurrencyConvertResponse convertResponse = exchangeRateService.convertToVND(BigDecimal.valueOf(total), type);
                if (convertResponse != null && convertResponse.getConvertedAmount() != null) {
                    totalPriceEstimate = convertResponse.getConvertedAmount().doubleValue();
                }
            }

            dto.setTotalPriceEstimate(totalPriceEstimate);

            log.info("createOrUpdateQuotation() - End | subRequestId: {} - totalPriceEstimate: {}", input.getSubRequestId(), totalPriceEstimate);
            return dto;

        }catch (Exception e) {
            log.error("createOrUpdateQuotation() - Exception: {}", e.getMessage());
            throw AppException.builder()
                    .message("Lỗi khi tạo báo giá: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }



    public double calculateTotalPrice(List<QuotationDetailDTO> details) {
        double total = 0;
        for (QuotationDetailDTO d : details) {
            double lineTotal = d.getBasePrice() + d.getServiceFee() + d.getShippingEstimate();
            if (d.getTaxAmounts() != null) {
                for (Double v : d.getTaxAmounts().values()) {
                    lineTotal += v;
                }
            }
            total += lineTotal;
        }
        return total;
    }



    @Override
    public QuotationDTO getQuotationById(String quotationId) {
        UUID id = UUID.fromString(quotationId);
        Quotation quotation = quotationBusiness.getById(id)
                .orElseThrow(() -> AppException.builder().message("Không tìm thấy Quotation").code(404).build());

        QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);
        dto.setDetails(
                quotation.getDetails()
                        .stream()
                        .map(m -> modelMapper.map(m, QuotationDetailDTO.class))
                        .toList()
        );
        return dto;
    }
}
