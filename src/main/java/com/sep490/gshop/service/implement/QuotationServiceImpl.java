package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.QuotationType;
import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import com.sep490.gshop.payload.dto.*;
import com.sep490.gshop.payload.request.quotation.*;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestModel;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.ExchangeRateService;
import com.sep490.gshop.service.QuotationService;
import com.sep490.gshop.service.TaxRateService;
import com.sep490.gshop.utils.CalculationUtil;
import com.sep490.gshop.utils.MapperUtil;
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
    private BusinessManagerBusiness businessManagerBusiness;
    @Autowired
    public QuotationServiceImpl(QuotationBusiness quotationBusiness, SubRequestBusiness subRequestBusiness, RequestItemBusiness requestItemBusiness,
                                TaxRateBusiness taxRateBusiness, HsCodeBusiness hsCodeBusiness, ModelMapper modelMapper
    , TaxRateService taxRateService, ExchangeRateService exchangeRateService, PurchaseRequestBusiness purchaseRequestBusiness, BusinessManagerBusiness businessManagerBusiness) {
        this.quotationBusiness = quotationBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.taxRateBusiness = taxRateBusiness;
        this.hsCodeBusiness = hsCodeBusiness;
        this.modelMapper = modelMapper;
        this.taxRateService = taxRateService;
        this.exchangeRateService = exchangeRateService;
        this.purchaseRequestBusiness = purchaseRequestBusiness;
        this.businessManagerBusiness = businessManagerBusiness;
    }
    @PostConstruct
    public void init() {
        this.calculationUtil = new CalculationUtil(exchangeRateService);
    }

    @Override
    public QuotationCalculatedDTO calculateOfflineQuotationInternal(OffineQuotationRequest input) {
        UUID subRequestId = UUID.fromString(input.getSubRequestId());
        SubRequest sub = subRequestBusiness.getById(subRequestId)
                .orElseThrow(() -> AppException.builder()
                        .message("Không tìm thấy sub request")
                        .code(404)
                        .build());

        // Kiểm tra số lượng details phải khớp số lượng requestItem
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
        var serviceRate = businessManagerBusiness.getConfig().getServiceFee();
        for (OfflineQuotationDetailRequest detailReq : input.getDetails()) {

            //Kiểm tra requestItem tồn tại
            RequestItem item = requestItemBusiness.getById(UUID.fromString(detailReq.getRequestItemId()))
                    .orElseThrow(() -> AppException.builder()
                            .message("RequestItem không tìm thấy: " + detailReq.getRequestItemId())
                            .code(404)
                            .build());

            //Kiểm tra thuộc về subRequest
            if (!item.getSubRequest().getId().equals(subRequestId)) {
                throw AppException.builder()
                        .message("Request item không thuộc về sub request này")
                        .code(400)
                        .build();
            }

            //Kiểm tra hsCode
            HsCode hsCode = hsCodeBusiness.getById(detailReq.getHsCodeId())
                    .orElseThrow(() -> AppException.builder()
                            .message("HsCode không tìm thấy: " + detailReq.getHsCodeId())
                            .code(404)
                            .build());

            //Lấy thuế áp dụng
            List<TaxRate> taxRates = taxRateBusiness.findTaxRateHsCodeAndRegion(hsCode, detailReq.getRegion());

            var serviceFee = serviceRate * detailReq.getBasePrice();

            TaxCalculationResult taxResult = calculationUtil.calculateTaxes(detailReq.getBasePrice(), taxRates);

            //Tính tổng trước quy đổi
            double totalDetail = calculationUtil.calculateTotalPrice(
                    detailReq.getBasePrice(),
                    serviceFee,
                    taxResult.getTaxAmounts()
            );
            double totalDetailWithQuantity = totalDetail * item.getQuantity();
            //Xử lý tiền tệ & tỷ giá
            String currency = detailReq.getCurrency() != null
                    ? detailReq.getCurrency().toUpperCase(Locale.ROOT)
                    : "USD";

            double totalVNPrice;
            double exchangeRate;

            if (!"VND".equalsIgnoreCase(currency)) {
                BigDecimal converted = calculationUtil.convertToVND(BigDecimal.valueOf(totalDetailWithQuantity), currency);
                totalVNPrice = converted.doubleValue();
                exchangeRate = converted.doubleValue() / totalDetailWithQuantity;
            } else {
                totalVNPrice = totalDetailWithQuantity;
                exchangeRate = 1.0;
            }

            //Map danh sách thuế thành DTO
            List<TaxRateSnapshotDTO> taxRatesDTO = taxRates.stream()
                    .map(rate -> {
                        TaxRateSnapshotDTO dto = new TaxRateSnapshotDTO();
                        dto.setTaxType(rate.getTaxType());
                        dto.setRate(rate.getRate());
                        dto.setRegion(rate.getRegion());
                        dto.setTaxName(rate.getTaxName());
                        return dto;
                    }).toList();

            //Tạo detail DTO
            QuotationDetailCalculatedDTO detailDTO = new QuotationDetailCalculatedDTO();
            detailDTO.setRequestItemId(detailReq.getRequestItemId());
            detailDTO.setProductName(item.getProductName());
            detailDTO.setBasePrice(detailReq.getBasePrice());
            detailDTO.setServiceFee(serviceFee);
            detailDTO.setServiceRate(serviceRate);
            detailDTO.setCurrency(currency);
            detailDTO.setExchangeRate(exchangeRate);
            detailDTO.setTaxAmounts(taxResult.getTaxAmounts());
            detailDTO.setTotalTaxAmount(taxResult.getTotalTax());
            detailDTO.setTotalPriceBeforeExchange(totalDetail);
            detailDTO.setTotalVNDPrice(totalVNPrice);
            detailDTO.setNote(detailReq.getNote());
            detailDTO.setHsCode(detailReq.getHsCodeId());
            detailDTO.setTaxRates(taxRatesDTO);

            detailDTOs.add(detailDTO);
        }

        //Tính tổng báo giá
        double total = detailDTOs.stream()
                .mapToDouble(QuotationDetailCalculatedDTO::getTotalVNDPrice)
                .sum();

        //Map ra QuotationCalculatedDTO
        QuotationCalculatedDTO dto = new QuotationCalculatedDTO();
        dto.setDetails(detailDTOs);
        dto.setSubRequestId(input.getSubRequestId());
        dto.setTotalPriceEstimate(total);
        dto.setShippingEstimate(input.getShippingEstimate());
        dto.setTotalWeightEstimate(input.getTotalWeightEstimate()); //
        dto.setPackageType(input.getPackageType()); //
        dto.setQuotationType(QuotationType.OFFLINE); //
        dto.setShipper(input.getShipper()); //
        dto.setRecipient(input.getRecipient()); //
        dto.setNote(input.getNote());
        dto.setExpiredDate(input.getExpiredDate());

        return dto;
    }


    @Override
    public MessageResponse rejectQuotation(RejectQuotationRequest rejectQuotationRequest) {
        try {
            log.debug("rejectQuotation() QuotationServiceImpl Start | subRequestId: {}", rejectQuotationRequest.getSubRequestId());
            SubRequest subRequest = subRequestBusiness.getById(UUID.fromString(rejectQuotationRequest.getSubRequestId()))
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy yêu cầu để từ chối")
                            .code(404)
                            .build());
            if (SubRequestStatus.PENDING.compareTo(subRequest.getStatus()) < 0 ) {
                log.error("rejectQuotation() QuotationServiceImpl Error | subRequestId: {}, status: {}",
                        subRequest.getId(), subRequest.getStatus());
                throw new AppException(400, "Yêu cầu đã được xử lý, không thể từ chối");
            }
            subRequest.setStatus(SubRequestStatus.REJECTED);
            subRequest.setRejectionReason(rejectQuotationRequest.getRejectionReason());
            SubRequest updatedSubRequest = subRequestBusiness.update(subRequest);
            updatePurchaseRequestStatus(subRequest.getId());
            log.debug("rejectQuotation() QuotationServiceImpl End | subRequestId: {}",
                    updatedSubRequest.getId());
            return MessageResponse.builder()
                    .message("Đã từ chối yêu cầu báo giá")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("rejectQuotation() QuotationServiceImpl Exception | message: {}", e.getMessage());
            throw AppException.builder()
                    .message("Lỗi khi từ chối báo giá: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }

    @Override
    @Transactional
    public OnlineQuotationDTO createOnlineQuotation(@Valid OnlineQuotationRequest request) {
        log.debug("createOnlineQuotation() - Start | subRequestId: {}", request.getSubRequestId());

        UUID subRequestId = UUID.fromString(request.getSubRequestId());

        // 1. Check SubRequest tồn tại
        SubRequest sub = subRequestBusiness.getById(subRequestId)
                .orElseThrow(() -> AppException.builder()
                        .message("Không tìm thấy sub request")
                        .code(404)
                        .build());

        // 2. Check chưa tồn tại OnlineQuotation cho subRequest
        if (quotationBusiness.findBySubRequest(subRequestId).isPresent()) {
            throw AppException.builder()
                    .message("OnlineQuotation đã tồn tại cho subRequest này")
                    .code(400)
                    .build();
        }

        // 3. Validate số lượng details
        int totalRequestItems = sub.getRequestItems().size();
        if (request.getDetails().size() < totalRequestItems) {
            throw AppException.builder()
                    .message("Phải điền đủ thông tin cho tất cả request item")
                    .code(400)
                    .build();
        } else if (request.getDetails().size() > totalRequestItems) {
            throw AppException.builder()
                    .message("Request items bị dư so với subRequest")
                    .code(400)
                    .build();
        }

        // 4. Tạo Quotation entity ONLINE
        Quotation quotation = new Quotation();
        quotation.setSubRequest(sub);
        quotation.setNote(request.getNote());
        quotation.setExpiredDate(request.getExpiredDate());
        quotation.setFees(request.getFees());
        quotation.setQuotationType(QuotationType.ONLINE);
        quotation.setTotalPriceBeforeExchange(request.getTotalPriceBeforeExchange());

        List<QuotationDetail> detailEntities = new ArrayList<>();

        // 5. Map chi tiết sản phẩm
        var serviceRate = businessManagerBusiness.getConfig().getServiceFee();
        double totalItemsVNDPrice =  0;

        for (OnlineQuotationDetailRequest d : request.getDetails()) {
            RequestItem item = requestItemBusiness.getById(UUID.fromString(d.getRequestItemId()))
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy request item: " + d.getRequestItemId())
                            .code(404)
                            .build());

            if (!item.getSubRequest().getId().equals(subRequestId)) {
                throw AppException.builder()
                        .message("Request item không thuộc subRequest này")
                        .code(400)
                        .build();
            }

            double serviceFee = serviceRate * d.getBasePrice();
            double itemTotalBeforeExchange = (d.getBasePrice() + serviceFee) * item.getQuantity();
            //
            double itemTotalVND = itemTotalBeforeExchange;
            double exchangeRate = 1.0;

            if (d.getCurrency() != null && !"VND".equalsIgnoreCase(d.getCurrency())) {
                BigDecimal converted = calculationUtil.convertToVND(BigDecimal.valueOf(itemTotalBeforeExchange), d.getCurrency());
                itemTotalVND = converted.doubleValue();
                exchangeRate = converted.doubleValue() / itemTotalBeforeExchange;
            }

            QuotationDetail detail = new QuotationDetail();
            detail.setQuotation(quotation);
            detail.setRequestItem(item);
            detail.setCurrency(d.getCurrency());
            detail.setBasePrice(d.getBasePrice());
            detail.setServiceFee(serviceFee);
            detail.setExchangeRate(exchangeRate);
            detail.setTotalVNDPrice(itemTotalVND);
            detail.setServiceRate(serviceRate);
            detailEntities.add(detail);
            totalItemsVNDPrice += itemTotalVND;
        }
        quotation.setDetails(detailEntities);

        // 6. Tính totalPriceEstimate từ totalPriceBeforeExchange (convert 1 lần)
        double totalPriceEstimate = 0;
        double shippingEstimate = request.getShippingEstimate();
        double exchangeRate = 1.0;
        String currency = !request.getDetails().isEmpty() ? request.getDetails().get(0).getCurrency() : null;

        if (currency != null && !"VND".equalsIgnoreCase(currency)) {
            BigDecimal convertedShip = calculationUtil.convertToVND(BigDecimal.valueOf(request.getShippingEstimate()), currency);
            shippingEstimate = convertedShip.doubleValue();
            exchangeRate = totalPriceEstimate/request.getTotalPriceBeforeExchange();
        }
        double otherFeesVND = 0.0;
        if (request.getFees() != null) {
            for (String fee : request.getFees()) {
                // Tách số từ chuỗi, ví dụ "Phí vận chuyển quốc tế: 43.93 USD"
                java.util.regex.Matcher matcher = java.util.regex.Pattern
                        .compile("(\\d+(?:\\.\\d+)?)")
                        .matcher(fee);
                if (matcher.find()) {
                    double value = Double.parseDouble(matcher.group(1));
                    if (currency != null && !"VND".equalsIgnoreCase(currency)) {
                        BigDecimal convertedFee = calculationUtil.convertToVND(BigDecimal.valueOf(value), currency);
                        otherFeesVND += convertedFee.doubleValue();
                    } else {
                        otherFeesVND += value;
                    }
                }
            }
        }

// === Tổng VNĐ cuối cùng ===
        totalPriceEstimate = totalItemsVNDPrice + shippingEstimate + otherFeesVND;
        quotation.setShippingEstimate(shippingEstimate);
        // chua luu dc phải totalItemsVNDPrice + shippingVND + phí khác VND
        quotation.setTotalPriceEstimate(totalPriceEstimate);
        // 7. Lưu quotation
        quotationBusiness.create(quotation);

        // 8. Update trạng thái subRequest
        sub.setStatus(SubRequestStatus.QUOTED);
        subRequestBusiness.update(sub);

        // 9. Map trả về DTO (map thủ công phần details)
        OnlineQuotationDTO dto = modelMapper.map(quotation, OnlineQuotationDTO.class);
        dto.setSubRequestId(request.getSubRequestId());
        dto.setQuotationType(QuotationType.ONLINE);
        dto.setSubRequestStatus(sub.getStatus());
        List<OnlineQuotationDetailDTO> detailDTOs = quotation.getDetails().stream()
                .map(detail -> {
                    OnlineQuotationDetailDTO dDto = new OnlineQuotationDetailDTO();
                    dDto.setRequestItemId(detail.getRequestItem() != null ? detail.getRequestItem().getId().toString() : null);
                    dDto.setCurrency(detail.getCurrency());
                    dDto.setBasePrice(detail.getBasePrice());
                    dDto.setServiceFee(detail.getServiceFee());
                    dDto.setTotalVNPrice(detail.getTotalVNDPrice());
                    dDto.setCurrency(detail.getCurrency());
                    dDto.setServiceRate(detail.getServiceRate());
                    dDto.setExchangeRate(detail.getExchangeRate());
                    for (OnlineQuotationDetailRequest d : request.getDetails()) {
                        RequestItem item = requestItemBusiness.getById(UUID.fromString(d.getRequestItemId()))
                                .orElseThrow(() -> AppException.builder()
                                        .message("Không tìm thấy request item: " + d.getRequestItemId())
                                        .code(404)
                                        .build());
                        dDto.setQuantity(item.getQuantity());
                    }

                    // Nếu DTO của bạn có field totalVNPrice thì set:
                    try {
                        dDto.getClass().getMethod("setTotalVNPrice", Double.class)
                                .invoke(dDto, detail.getTotalVNDPrice());
                    } catch (Exception ignored) {}
                    return dDto;
                })
                .toList();

        dto.setDetails(detailDTOs);

        log.debug("createOnlineQuotation() - End | subRequestId: {}", request.getSubRequestId());
        return dto;
    }





    @Override
    @Transactional
    public OfflineQuotationDTO createOfflineQuotation(@Valid OffineQuotationRequest input) {
        log.debug("createQuotation() - Start | subRequestId: {}", input.getSubRequestId());
        try {
            UUID subRequestId = UUID.fromString(input.getSubRequestId());

            // Kiểm tra trùng quotation
            if (quotationBusiness.findBySubRequest(subRequestId).isPresent()) {
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

            // Tạo đối tượng Quotation
            Quotation quotation = new Quotation();
            quotation.setSubRequest(sub);
            quotation.setNote(input.getNote());
            quotation.setExpiredDate(input.getExpiredDate());
            quotation.setShippingEstimate(input.getShippingEstimate());
            quotation.setTotalWeightEstimate(input.getTotalWeightEstimate());
            quotation.setPackageType(input.getPackageType());
            quotation.setQuotationType(QuotationType.OFFLINE);
            quotation.setShipper(input.getShipper());
            quotation.setRecipient(input.getRecipient());

            quotationBusiness.create(quotation);

            List<QuotationDetail> detailEntities = new ArrayList<>();
            List<OfflineQuotationDetailDTO> detailDTOs = new ArrayList<>();
            var serviceRate = businessManagerBusiness.getConfig().getServiceFee();
            for (OfflineQuotationDetailRequest detailReq : input.getDetails()) {

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

                // Gắn hsCode thực thể
                HsCode hsCode = hsCodeBusiness.getById(detailReq.getHsCodeId())
                        .orElseThrow(() -> AppException.builder()
                                .message("HsCode không tìm thấy: " + detailReq.getHsCodeId())
                                .code(404)
                                .build());

                // Map taxRates sang snapshot
                List<TaxRate> taxRates = taxRateBusiness.findTaxRateHsCodeAndRegion(hsCode, detailReq.getRegion());
                List<TaxRateSnapshot> snapshots = taxRates.stream()
                        .map(rate -> modelMapper.map(rate, TaxRateSnapshot.class))
                        .toList();
                detail.setTaxRates(snapshots);

                var serviceFee = serviceRate * detailReq.getBasePrice();

                TaxCalculationResult taxResult = calculationUtil.calculateTaxes(detailReq.getBasePrice(), taxRates);

                //Tính tổng trước quy đổi
                double totalDetail = calculationUtil.calculateTotalPrice(
                        detailReq.getBasePrice(),
                        serviceFee,
                        taxResult.getTaxAmounts()
                );
                double totalDetailWithQuantity = totalDetail * item.getQuantity();

                // Currency & ExchangeRate
                String currency = detailReq.getCurrency() != null
                        ? detailReq.getCurrency().toUpperCase(Locale.ROOT)
                        : "USD";

                double totalVNPrice;
                double exchangeRate;

                if (!"VND".equalsIgnoreCase(currency)) {
                    BigDecimal converted = calculationUtil.convertToVND(BigDecimal.valueOf(totalDetailWithQuantity), currency);
                    totalVNPrice = converted.doubleValue();
                    exchangeRate = converted.doubleValue() / totalDetailWithQuantity;
                } else {
                    totalVNPrice = totalDetailWithQuantity;
                    exchangeRate = 1.0;
                }

                // Set vào detail
                detail.setExchangeRate(exchangeRate);
                detail.setCurrency(currency);
                detail.setTotalVNDPrice(totalVNPrice);
                detail.setHsCode(hsCode.getHsCode());
                detail.setBasePrice(detailReq.getBasePrice());
                detail.setServiceRate(serviceRate);
                detail.setServiceFee(serviceFee);
                detailEntities.add(detail);

                // Map ra DTO để trả về
                OfflineQuotationDetailDTO detailDTO = modelMapper.map(detail, OfflineQuotationDetailDTO.class);
                detailDTO.setTaxAmounts(taxResult.getTaxAmounts());
                detailDTO.setRequestItemId(detailReq.getRequestItemId());
                detailDTO.setTotalVNDPrice(totalVNPrice);
                detailDTO.setTotalTaxAmount(taxResult.getTotalTax());
                detailDTO.setTotalPriceBeforeExchange(totalDetail);
                detailDTOs.add(detailDTO);
            }

            quotation.setDetails(detailEntities);

            // Tổng giá trị báo giá
            double total = detailDTOs.stream()
                    .mapToDouble(OfflineQuotationDetailDTO::getTotalVNDPrice)
                    .sum();
            quotation.setTotalPriceEstimate(total);

            quotationBusiness.update(quotation);

            // Cập nhật trạng thái SubRequest và PurchaseRequest
            sub.setStatus(SubRequestStatus.QUOTED);
            subRequestBusiness.update(sub);

            PurchaseRequest purchaseRequest =
                    purchaseRequestBusiness.findPurchaseRequestBySubRequestId(subRequestId);
            purchaseRequest.setStatus(PurchaseRequestStatus.QUOTED);
            purchaseRequest.getHistory().add(
                    new PurchaseRequestHistory(purchaseRequest,"Yêu cầu đã được báo giá")
            );
            purchaseRequestBusiness.update(purchaseRequest);

            // Đóng gói DTO trả về
            OfflineQuotationDTO dto = modelMapper.map(quotation, OfflineQuotationDTO.class);
            dto.setDetails(detailDTOs);
            dto.setSubRequestId(input.getSubRequestId());
            dto.setTotalPriceEstimate(total);
            dto.setShippingEstimate(input.getShippingEstimate());
            dto.setTotalWeightEstimate(input.getTotalWeightEstimate());
            dto.setPackageType(input.getPackageType());
            dto.setQuotationType(QuotationType.OFFLINE);
            dto.setShipper(input.getShipper());
            dto.setSubRequestStatus(SubRequestStatus.QUOTED);
            dto.setRecipient(input.getRecipient());

            log.debug("createQuotation() - End | subRequestId: {}", input.getSubRequestId());
            return dto;

        } catch (Exception e) {
            log.error("createQuotation() - Exception: {}", e.getMessage(), e);
            throw AppException.builder()
                    .message("Lỗi khi tạo báo giá: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }

    @Override
    public List<OfflineQuotationDTO> findAllQuotations() {
        log.debug("findAllQuotations() - Start");
        try {
            List<Quotation> quotations = quotationBusiness.getAll();
            List<OfflineQuotationDTO> dtos = new ArrayList<>();

            for (Quotation quotation : quotations) {
                OfflineQuotationDTO dto = modelMapper.map(quotation, OfflineQuotationDTO.class);

                List<OfflineQuotationDetailDTO> detailDTOs = new ArrayList<>();
                for (QuotationDetail detail : quotation.getDetails()) {
                    OfflineQuotationDetailDTO detailDTO = enrichQuotationDetailDto(detail);
                    detailDTOs.add(detailDTO);
                }
                dto.setDetails(detailDTOs);

                double total = detailDTOs.stream()
                        .mapToDouble(OfflineQuotationDetailDTO::getTotalVNDPrice)
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
    public OfflineQuotationDTO getQuotationById(String quotationId) {
        log.debug("getQuotationById() - Start | quotationId: {}", quotationId);
        try {
            UUID id = UUID.fromString(quotationId);
            Quotation quotation = quotationBusiness.getById(id)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy Quotation")
                            .code(404)
                            .build());

            OfflineQuotationDTO dto = modelMapper.map(quotation, OfflineQuotationDTO.class);

            List<OfflineQuotationDetailDTO> detailDTOs = new ArrayList<>();
            for (QuotationDetail detail : quotation.getDetails()) {
                OfflineQuotationDetailDTO detailDTO = enrichQuotationDetailDto(detail);
                detailDTOs.add(detailDTO);
            }
            double total = detailDTOs.stream().mapToDouble(OfflineQuotationDetailDTO::getTotalVNDPrice).sum();
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




    private OfflineQuotationDetailDTO enrichQuotationDetailDto(QuotationDetail detail) {
        OfflineQuotationDetailDTO detailDTO = modelMapper.map(detail, OfflineQuotationDetailDTO.class);

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

    private void updatePurchaseRequestStatus(UUID subrequestId) {
        PurchaseRequest purchaseRequest = purchaseRequestBusiness.findPurchaseRequestBySubRequestId(subrequestId);
        PurchaseRequestModel purchaseRequestModel = MapperUtil.convertToPurchaseRequestModel(purchaseRequest);
        if (!purchaseRequestModel.getRequestItems().isEmpty()) {
            return;
        }
        long count = purchaseRequestModel.getSubRequests().stream().filter(sub -> !SubRequestStatus.REJECTED.equals(sub.getStatus())).count();
        if (count == 0) {
            purchaseRequest.setStatus(PurchaseRequestStatus.CANCELLED);
            PurchaseRequestHistory purchaseRequestHistory = new PurchaseRequestHistory(purchaseRequest,"Yêu cầu đã bị đóng do không thể báo giá");
            purchaseRequest.getHistory().add(purchaseRequestHistory);
            purchaseRequestBusiness.update(purchaseRequest);
        }
    }


}
