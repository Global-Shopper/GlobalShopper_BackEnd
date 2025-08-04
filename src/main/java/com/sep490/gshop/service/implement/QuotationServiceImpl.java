package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import com.sep490.gshop.payload.dto.*;
import com.sep490.gshop.payload.request.QuotationDetailRequest;
import com.sep490.gshop.payload.request.QuotationRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.ExchangeRateService;
import com.sep490.gshop.service.QuotationService;
import com.sep490.gshop.service.TaxRateService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.CalculationUtil;
import jakarta.annotation.PostConstruct;
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
    private final PurchaseRequestBusiness purchaseRequestBusiness;
    private QuotationBusiness quotationBusiness;
    private SubRequestBusiness subRequestBusiness;
    private RequestItemBusiness requestItemBusiness;
    private TaxRateBusiness taxRateBusiness;
    private HsCodeBusiness hsCodeBusiness;
    private ModelMapper modelMapper;
    private TaxRateService taxRateService;
    private ExchangeRateService exchangeRateService;
    private CalculationUtil calculationUtil;
    private UserBusiness userBusiness;
    @Autowired
    public QuotationServiceImpl(QuotationBusiness quotationBusiness, SubRequestBusiness subRequestBusiness, RequestItemBusiness requestItemBusiness,
                                TaxRateBusiness taxRateBusiness, HsCodeBusiness hsCodeBusiness, ModelMapper modelMapper
    , TaxRateService taxRateService, ExchangeRateService exchangeRateService, UserBusiness userBusiness, PurchaseRequestBusiness purchaseRequestBusiness) {
        this.quotationBusiness = quotationBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.taxRateBusiness = taxRateBusiness;
        this.hsCodeBusiness = hsCodeBusiness;
        this.modelMapper = modelMapper;
        this.taxRateService = taxRateService;
        this.exchangeRateService = exchangeRateService;
        this.userBusiness = userBusiness;
        this.purchaseRequestBusiness = purchaseRequestBusiness;
    }
    @PostConstruct
    public void init() {
        this.calculationUtil = new CalculationUtil(exchangeRateService);
    }

    public QuotationCalculatedDTO calculateQuotationInternal(QuotationRequest input) {
        UUID subRequestId = UUID.fromString(input.getSubRequestId());
        SubRequest sub = subRequestBusiness.getById(subRequestId)
                .orElseThrow(() -> AppException.builder()
                        .message("Không tìm thấy sub request")
                        .code(404)
                        .build());

        int totalRequestItems = sub.getRequestItems().size();
        if (input.getDetails().size() < totalRequestItems) {
            throw AppException.builder()
                    .message("Bạn cần điền đủ thông tin của các request item")
                    .code(400)
                    .build();
        } else if (input.getDetails().size() > totalRequestItems) {
            throw AppException.builder()
                    .message("Request items không nằm trong sub request (bị dư)")
                    .code(400)
                    .build();
        }

        List<QuotationDetailCalculatedDTO> detailDTOs = new ArrayList<>();

        for (QuotationDetailRequest detailReq : input.getDetails()) {
            RequestItem item = requestItemBusiness.getById(UUID.fromString(detailReq.getRequestItemId()))
                    .orElseThrow(() -> AppException.builder()
                            .message("RequestItem không tìm thấy: " + detailReq.getRequestItemId())
                            .code(404)
                            .build());

            if (!item.getSubRequest().getId().equals(subRequestId)) {
                throw AppException.builder()
                        .message("Request item không thuộc về sub request này")
                        .code(400)
                        .build();
            }

            HsCode hsCode = hsCodeBusiness.getById(detailReq.getHsCodeId())
                    .orElseThrow(() -> AppException.builder()
                            .message("HsCode không tìm thấy: " + detailReq.getHsCodeId())
                            .code(404)
                            .build());

            List<TaxRate> taxRates = taxRateBusiness.findTaxRateHsCodeAndRegion(hsCode, detailReq.getRegion());
            double basePriceWithQuantity = detailReq.getBasePrice() * item.getQuantity();

            TaxCalculationResult taxResult = calculationUtil.calculateTaxes(basePriceWithQuantity, taxRates);

            double totalDetail = calculationUtil.calculateTotalPrice(
                    basePriceWithQuantity,
                    detailReq.getServiceFee(),
                    taxResult.getTaxAmounts()
            );

            String currency = detailReq.getCurrency() != null ? detailReq.getCurrency().toUpperCase(Locale.ROOT) : "USD";
            double totalVNPrice = totalDetail;
            double exchangeRate = 1.0;
            if (!"VND".equalsIgnoreCase(currency)) {
                BigDecimal converted = calculationUtil.convertToVND(BigDecimal.valueOf(totalDetail), currency);
                totalVNPrice = converted.doubleValue();
                exchangeRate = converted.doubleValue() / totalDetail;
            } else {
                throw AppException.builder()
                        .message("Không thể chuyển từ ngoại tệ " + currency + " sang VND")
                        .code(400)
                        .build();
            }

            // MAP taxRates snapshot sang DTO (giả định có TaxRateSnapshotDTO)
            List<TaxRateSnapshotDTO> taxRatesDTO = taxRates.stream()
                    .map(rate -> {
                        TaxRateSnapshotDTO dto = new TaxRateSnapshotDTO();
                        dto.setTaxType(rate.getTaxType().toString());
                        dto.setRate(rate.getRate());
                        dto.setRegion(rate.getRegion());
                        return dto;
                    }).toList();

            QuotationDetailCalculatedDTO detailDTO = new QuotationDetailCalculatedDTO();
            detailDTO.setRequestItemId(detailReq.getRequestItemId());
            detailDTO.setBasePrice(detailReq.getBasePrice());
            detailDTO.setServiceFee(detailReq.getServiceFee());
            detailDTO.setCurrency(currency);
            detailDTO.setExchangeRate(exchangeRate);
            detailDTO.setTaxAmounts(taxResult.getTaxAmounts());
            detailDTO.setTotalTaxAmount(taxResult.getTotalTax());
            detailDTO.setTotalPriceBeforeExchange(totalDetail);
            detailDTO.setTotalVNDPrice(totalVNPrice);
            detailDTO.setNote(detailReq.getNote());
            detailDTO.setTaxRates(taxRatesDTO);
            detailDTOs.add(detailDTO);
        }

        double total = detailDTOs.stream()
                .mapToDouble(QuotationDetailCalculatedDTO::getTotalVNDPrice)
                .sum();

        QuotationCalculatedDTO dto = new QuotationCalculatedDTO();
        dto.setDetails(detailDTOs);
        dto.setSubRequestId(input.getSubRequestId());
        total += input.getShippingEstimate();
        dto.setTotalPriceEstimate(total);
        dto.setShippingEstimate(input.getShippingEstimate());
        dto.setNote(input.getNote());
        dto.setExpiredDate(input.getExpiredDate());

        return dto;
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

            int totalRequestItems = sub.getRequestItems().size();
            if (input.getDetails().size() < totalRequestItems) {
                throw AppException.builder()
                        .message("Bạn cần điền đủ thông tin của các request item mới được tạo báo giá")
                        .code(400)
                        .build();
            } else if (input.getDetails().size() > totalRequestItems) {
                throw AppException.builder()
                        .message("Request items không nằm trong sub request (bị dư)")
                        .code(400)
                        .build();
            }

            quotationBusiness.create(quotation);

            List<QuotationDetail> detailEntities = new ArrayList<>();
            List<QuotationDetailDTO> detailDTOs = new ArrayList<>();

            for (QuotationDetailRequest detailReq : input.getDetails()) {
                RequestItem item = requestItemBusiness.getById(UUID.fromString(detailReq.getRequestItemId()))
                        .orElseThrow(() -> AppException.builder()
                                .message("RequestItem không tìm thấy: " + detailReq.getRequestItemId())
                                .code(404)
                                .build());

                if (!item.getSubRequest().getId().equals(subRequestId)) {
                    throw AppException.builder()
                            .message("Request item không thuộc về sub request này")
                            .code(400)
                            .build();
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
                var basePriceWithQuantity = detailReq.getBasePrice() * item.getQuantity();
                TaxCalculationResult taxResult = calculationUtil.calculateTaxes(basePriceWithQuantity, taxRates);

                double totalDetail = calculationUtil.calculateTotalPrice(
                        basePriceWithQuantity,
                        detailReq.getServiceFee(),
                        taxResult.getTaxAmounts()
                );

                String currency = detailReq.getCurrency() != null ? detailReq.getCurrency().toUpperCase(Locale.ROOT) : "USD";

                double totalVNPrice = totalDetail;
                if (!"VND".equalsIgnoreCase(currency)) {
                    BigDecimal converted = calculationUtil.convertToVND(BigDecimal.valueOf(totalDetail), currency);
                    totalVNPrice = converted.doubleValue();

                    detail.setExchangeRate(converted.doubleValue() / totalDetail);
                    detail.setCurrency(currency);
                } else {
                    throw AppException.builder().message("Bạn không thể chuyển đổi ngoại tệ từ VND sang VND").code(400).build();
                }

                detail.setTotalVNDPrice(totalVNPrice);

                detailEntities.add(detail);

                QuotationDetailDTO detailDTO = modelMapper.map(detail, QuotationDetailDTO.class);
                detailDTO.setTaxAmounts(taxResult.getTaxAmounts());
                detailDTO.setRequestItemId(detailReq.getRequestItemId());
                detailDTO.setTotalVNDPrice(totalVNPrice);
                detailDTO.setTotalTaxAmount(taxResult.getTotalTax());
                detailDTO.setTotalPriceBeforeExchange(totalDetail);
                detailDTOs.add(detailDTO);
            }

            quotation.setDetails(detailEntities);

            double total = detailDTOs.stream()
                    .mapToDouble(QuotationDetailDTO::getTotalVNDPrice)
                    .sum();
            total += input.getShippingEstimate();
            quotation.setTotalPriceEstimate(total);
            quotationBusiness.update(quotation);
            sub.setStatus(SubRequestStatus.QUOTED);
            subRequestBusiness.update(sub);
            QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);
            dto.setDetails(detailDTOs);
            dto.setSubRequestId(input.getSubRequestId());
            dto.setTotalPriceEstimate(total);
            dto.setShippingEstimate(input.getShippingEstimate());

            PurchaseRequest purchaseRequest = purchaseRequestBusiness.findPurchaseRequestBySubRequestId(subRequestId);
            purchaseRequest.setStatus(PurchaseRequestStatus.QUOTED);
            PurchaseRequestHistory purchaseRequestHistory = new PurchaseRequestHistory(purchaseRequest,"Yêu cầu đã được báo giá");
            purchaseRequest.getHistory().add(purchaseRequestHistory);
            purchaseRequestBusiness.update(purchaseRequest);
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

    @Override
    public List<QuotationDTO> findAllQuotations() {
        log.debug("findAllQuotations() - Start");
        try {
            List<Quotation> quotations = quotationBusiness.getAll();
            List<QuotationDTO> dtos = new ArrayList<>();

            for (Quotation quotation : quotations) {
                QuotationDTO dto = modelMapper.map(quotation, QuotationDTO.class);

                List<QuotationDetailDTO> detailDTOs = new ArrayList<>();
                for (QuotationDetail detail : quotation.getDetails()) {
                    QuotationDetailDTO detailDTO = enrichQuotationDetailDto(detail);
                    detailDTOs.add(detailDTO);
                }
                dto.setDetails(detailDTOs);

                double total = detailDTOs.stream()
                        .mapToDouble(QuotationDetailDTO::getTotalVNDPrice)
                        .sum();
                dto.setTotalPriceEstimate(total);

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

            List<QuotationDetailDTO> detailDTOs = new ArrayList<>();
            for (QuotationDetail detail : quotation.getDetails()) {
                QuotationDetailDTO detailDTO = enrichQuotationDetailDto(detail);
                detailDTOs.add(detailDTO);
            }
            double total = detailDTOs.stream().mapToDouble(QuotationDetailDTO::getTotalVNDPrice).sum();
            dto.setDetails(detailDTOs);
            dto.setTotalPriceEstimate(total);
            dto.setSubRequestId(quotation.getSubRequest().getId().toString());
            dto.setSubRequestStatus(quotation.getSubRequest().getStatus());
            log.debug("getQuotationById() - End | quotationId: {}", quotationId);
            return dto;

        } catch (AppException ae) {
            log.error("getQuotationById() - AppException: {}", ae.getMessage());
            throw ae;
        } catch (Exception e) {
            log.error("getQuotationById() - Exception: {}", e.getMessage());
            throw AppException.builder()
                    .message("Lỗi khi lấy Quotation: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }




    private QuotationDetailDTO enrichQuotationDetailDto(QuotationDetail detail) {
        QuotationDetailDTO detailDTO = modelMapper.map(detail, QuotationDetailDTO.class);

        UUID requestItemId = detail.getRequestItem() != null ? detail.getRequestItem().getId() : null;
        detailDTO.setRequestItemId(requestItemId.toString());
        List<TaxRate> taxRates = detail.getTaxRates() != null ?
                detail.getTaxRates().stream()
                        .map(snapshot -> {
                            TaxRate rate = new TaxRate();
                            rate.setTaxType(snapshot.getTaxType());
                            rate.setRate(snapshot.getRate());
                            rate.setRegion(snapshot.getRegion());
                            return rate;
                        }).toList() : List.of();
        TaxCalculationResult taxResult = taxRateService.calculateTaxes(detail.getBasePrice(), taxRates);
        detailDTO.setTaxAmounts(taxResult.getTaxAmounts());
        detailDTO.setTotalTaxAmount(taxResult.getTotalTax());
        double totalPriceBeforeExchange = detail.getBasePrice() + detail.getServiceFee();
        if (taxResult.getTaxAmounts() != null) {
            totalPriceBeforeExchange += taxResult.getTaxAmounts().values().stream().mapToDouble(Double::doubleValue).sum();
        }
        detailDTO.setTotalPriceBeforeExchange(totalPriceBeforeExchange);

        detailDTO.setCurrency(detail.getCurrency());
        detailDTO.setExchangeRate(detail.getExchangeRate());
        detailDTO.setNote(detail.getNote());
        return detailDTO;
    }




}
