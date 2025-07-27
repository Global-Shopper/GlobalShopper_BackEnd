package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.RequestItemBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.QuotationDetail;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.RequestItemService;
import com.sep490.gshop.service.TaxRateService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class RequestItemServiceImpl implements RequestItemService {
    private RequestItemBusiness requestItemBusiness;
    private ModelMapper modelMapper;
    private TaxRateService taxRateService;
    public RequestItemServiceImpl(RequestItemBusiness requestItemBusiness
    , ModelMapper modelMapper, TaxRateService taxRateService){
        this.requestItemBusiness = requestItemBusiness;
        this.modelMapper = modelMapper;
        this.taxRateService = taxRateService;
    }
    @Override
    public RequestItemDTO getRequestItem(UUID id) {
        var requestItemFound = requestItemBusiness.getById(id).orElseThrow(() -> AppException.builder().message("Không tìm thấy request item").code(404).build());
        var requestItemDTO = modelMapper.map(requestItemFound, RequestItemDTO.class);
        var quotationDetailDTO =  enrichQuotationDetailDto(requestItemFound.getQuotationDetail());
        requestItemDTO.setQuotationDetail(quotationDetailDTO);
        return requestItemDTO;
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
