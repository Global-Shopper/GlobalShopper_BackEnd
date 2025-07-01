package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.PurchaseRequestBusiness;
import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.request.PurchaseRequestModel;
import com.sep490.gshop.service.PurchaseRequestService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.StringFormatUtil;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestBusiness purchaseRequesetBusiness;
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final UserBusiness userBusiness;
    private final ModelMapper modelMapper;

    @Autowired
    public PurchaseRequestServiceImpl(PurchaseRequestBusiness purchaseRequesetBusiness,
                                      ShippingAddressBusiness shippingAddressBusiness, UserBusiness userBusiness,
                                      ModelMapper modelMapper) {
        this.purchaseRequesetBusiness = purchaseRequesetBusiness;
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.userBusiness = userBusiness;
        this.modelMapper = modelMapper;
    }


    @Override
    public PurchaseRequestDTO createPurchaseRequest(PurchaseRequestModel purchaseRequestModel) {
        try {
            log.debug("createPurchaseRequest() PurchaseRequestServiceImpl start | request : {}", purchaseRequestModel);
            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(purchaseRequestModel.getShippingAddressId()))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy địa chỉ nhận hàng"));

            User user = userBusiness.getById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng hiện tại"));

            if (user instanceof Customer customer) {
                PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                        .shippingAddress(shippingAddress)
                        .customer(customer)
                        .status(PurchaseRequestStatus.SENT)
                        .build();
                purchaseRequest.setAdmin(null);
                purchaseRequest.setExpiredAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                PurchaseRequest finalPurchaseRequest = purchaseRequest;
                List<RequestItem> requestItems = purchaseRequestModel.getItems().stream()
                        .map(item -> RequestItem.builder()
                                .productName(item.getName())
                                .purchaseRequest(finalPurchaseRequest)
                                .productURL(item.getLink())
                                .quantity(item.getQuantity())
                                .description(item.getNote())
                                .variants(item.getVariants())
                                .build())
                        .toList();
                purchaseRequest.setRequestItems(requestItems);
                purchaseRequest = purchaseRequesetBusiness.create(purchaseRequest);
                PurchaseRequestDTO purchaseRequestDTO = modelMapper.map(purchaseRequest, PurchaseRequestDTO.class);
                log.debug("createPurchaseRequest() PurchaseRequestServiceImpl end | response : {}", purchaseRequestDTO);
                return purchaseRequestDTO;
            } else {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Người dùng hiện tại không phải là khách hàng");
            }
        } catch (Exception e) {
            log.error("createPurchaseRequest() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
        }
    }
}
