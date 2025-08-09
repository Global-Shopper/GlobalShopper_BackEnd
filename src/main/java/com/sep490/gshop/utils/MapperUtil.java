package com.sep490.gshop.utils;

import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.QuotationForPurchaseRequestDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.response.PurchaseRequestModel;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class MapperUtil {

    private final ModelMapper modelMapper = new ModelMapper();

    public PurchaseRequestModel convertToPurchaseRequestModel(PurchaseRequest purchaseRequest) {
        List<RequestItem> allItems = purchaseRequest.getRequestItems();
        long totalItemsWithQuotation = allItems.stream()
                .filter(item -> item.getQuotationDetail() != null)
                .count();
        // RequestItems without SubRequest
        List<RequestItemDTO> itemsWithoutSub = allItems.stream()
                .filter(item -> item.getSubRequest() == null)
                .map(item ->  modelMapper.map(item, RequestItemDTO.class))
                .toList();

        // Group by SubRequest
        Map<SubRequest, List<RequestItem>> subRequestMap = allItems.stream()
                .filter(item -> item.getSubRequest() != null)
                .collect(Collectors.groupingBy(RequestItem::getSubRequest));

        int paidSubRequestCount = (int) subRequestMap.keySet().stream()
                .filter(sub -> SubRequestStatus.PAID.equals(sub.getStatus()))
                .count();

        List<SubRequestDTO> subRequestModels = subRequestMap.entrySet().stream()
                .map(entry -> {
                    SubRequestDTO subDTO = modelMapper.map(entry.getKey(), SubRequestDTO.class);

                    if(entry.getKey().getQuotation() != null){
                        var quotationDTO = modelMapper.map(entry.getKey().getQuotation(), QuotationForPurchaseRequestDTO.class);
                        subDTO.setQuotationForPurchase(quotationDTO);
                    }
                    List<RequestItemDTO> requestItemDTOs = entry.getValue().stream()
                            .map(item ->modelMapper.map(item, RequestItemDTO.class))
                            .toList();
                    subDTO.setRequestItems(requestItemDTOs);
                    subDTO.setStatus(entry.getKey().getStatus());
                    return subDTO;
                })
                .toList();

        PurchaseRequestModel response = modelMapper.map(purchaseRequest, PurchaseRequestModel.class);
        response.setRequestItems(itemsWithoutSub);
        response.setSubRequests(subRequestModels);
        response.setItemsHasQuotation((int)totalItemsWithQuotation);
        response.setTotalItems(allItems.size());
        response.setPaidCount(paidSubRequestCount);
        return response;
    }
}
