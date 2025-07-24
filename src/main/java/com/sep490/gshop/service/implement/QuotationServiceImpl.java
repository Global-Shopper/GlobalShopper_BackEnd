package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import com.sep490.gshop.payload.dto.QuotationDTO;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.request.QuotationDetailRequest;
import com.sep490.gshop.payload.request.QuotationRequest;
import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.ExchangeRateService;
import com.sep490.gshop.service.QuotationService;
import com.sep490.gshop.service.TaxRateService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    public QuotationDTO createQuotation(@Valid QuotationRequest input) {
        log.debug("createQuotation() - Start | subRequestId: {}", input.getSubRequestId());
        try {
            UUID subRequestId = UUID.fromString(input.getSubRequestId());

            Optional<Quotation> optionalQuotation = quotationBusiness.findBySubRequest(subRequestId);
            if (optionalQuotation.isPresent()) {
                throw AppException.builder()
                        .message("Quotation đã tồn tại cho subRequestId này, không cho phép tạo lại")
                        .code(400)
                        .build();
            }

            SubRequest sub = subRequestBusiness.getById(subRequestId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy sub request")
                            .code(404)
                            .build());
            Quotation quotation = new Quotation();
            quotation.setSubRequest(sub);
            quotation.setNote(input.getNote());
            quotation.setExpiredDate(input.getExpiredDate());
            quotation.setShippingEstimate(input.getShippingEstimate());
            quotationBusiness.create(quotation);

            List<QuotationDetailDTO> detailDTOs = new ArrayList<>();
            for (QuotationDetailRequest detailReq : input.getDetails()) {
                RequestItem item = requestItemBusiness.getById(UUID.fromString(detailReq.getRequestItemId()))
                        .orElseThrow(() -> AppException.builder()
                                .message("RequestItem không tìm thấy: " + detailReq.getRequestItemId())
                                .code(404)
                                .build());

                boolean exists = quotationDetailBusiness.existsByQuotationAndRequestItem(quotation, item);
                if (exists) {
                    throw AppException.builder().message("Báo giá cho item này đã tồn tại !!!").code(400).build();
                }
                if(item.getSubRequest().getId() != subRequestId){
                    throw AppException.builder().message("Request item không thuộc về sub request này").code(400).build();
                }
                QuotationDetail detail = modelMapper.map(detailReq, QuotationDetail.class);
                detail.setQuotation(quotation);
                detail.setRequestItem(item);

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
                String currency = detailReq.getCurrency() != null ? detailReq.getCurrency().toUpperCase(Locale.ROOT) : "USD";
                double total = calculateTotalPrice(detailDTO);
                double totalPriceEstimate = total;
                if (!"VND".equalsIgnoreCase(currency)) {
                    CurrencyConvertResponse convertResponse = exchangeRateService.convertToVND(BigDecimal.valueOf(total), currency);
                    if (convertResponse != null && convertResponse.getConvertedAmount() != null) {
                        totalPriceEstimate = convertResponse.getConvertedAmount().doubleValue();
                    }
                }
                detailDTO.setTotalVNDPrice(totalPriceEstimate);
                detailDTOs.add(detailDTO);
            }
            QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);
            dto.setDetails(detailDTOs);
            dto.setSubRequestId(input.getSubRequestId());
            double total = 0;
            for(QuotationDetailDTO quotationDetailDTO : detailDTOs) {
                total += quotationDetailDTO.getTotalVNDPrice();
            }

            //total = total + dto.getShippingEstimate();
            dto.setTotalPriceEstimate(total);
            dto.setShippingEstimate(input.getShippingEstimate());
            log.debug("createQuotation() - End | subRequestId: {}", input.getSubRequestId());
            return dto;

        } catch (Exception e) {
            log.error("createQuotation() - Exception: {}", e.getMessage());
            throw AppException.builder()
                    .message("Lỗi khi tạo báo giá: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }


    public double calculateTotalPrice(QuotationDetailDTO detail) {
        double total = 0;
            double lineTotal = detail.getBasePrice() + detail.getServiceFee();
            if (detail.getTaxAmounts() != null) {
                for (Double v : detail.getTaxAmounts().values()) {
                    lineTotal += v;
                }
            }
            total += lineTotal;
        return total;
    }

    @Override
    public List<QuotationDTO> findAllQuotations() {
        log.debug("findAllQuotations() - Start");
        try {
            List<Quotation> quotations = quotationBusiness.getAll();
            List<QuotationDTO> dtos = new ArrayList<>();
            for (Quotation quotation : quotations) {
                QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);
                dto.setDetails(
                        quotation.getDetails()
                                .stream()
                                .map(detail -> modelMapper.map(detail, QuotationDetailDTO.class))
                                .toList()
                );
                dto.setSubRequestId(quotation.getSubRequest().getId().toString());

                dtos.add(dto);
            }
            log.debug("findAllQuotations() - End | total found: {}", dtos.size());
            return dtos;

        } catch (Exception e) {
            log.error("findAllQuotations() - Exception: {}", e.getMessage());
            throw AppException.builder()
                    .message("Lỗi khi lấy danh sách báo giá: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }

    @Override
    public QuotationDTO getQuotationById(String quotationId) {
        log.debug("getQuotationById() - Start | quotationId: {}", quotationId);
        try {
            UUID id = UUID.fromString(quotationId);
            Quotation quotation = quotationBusiness.getById(id)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy Quotation")
                            .code(404)
                            .build());

            QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);
            dto.setDetails(
                    quotation.getDetails()
                            .stream()
                            .map(m -> modelMapper.map(m, QuotationDetailDTO.class))
                            .toList()
            );
            dto.setSubRequestId(quotation.getSubRequest().getId().toString());

            log.debug("getQuotationById() - End | quotationId: {}", quotationId);
            return dto;

        }  catch (Exception e) {
            log.error("getQuotationById() - Exception: {}", e.getMessage());
            throw AppException.builder()
                    .message("Lỗi lấy báo giá: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }

}
