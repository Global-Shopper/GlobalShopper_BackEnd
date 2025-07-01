package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.PurchaseRequestBusiness;
import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.PurchaseRequestModel;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import com.sep490.gshop.service.PurchaseRequestService;
import com.sep490.gshop.utils.AuthUtils;
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

    private final PurchaseRequestBusiness purchaseRequestBusiness;
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final UserBusiness userBusiness;
    private final ModelMapper modelMapper;

    @Autowired
    public PurchaseRequestServiceImpl(PurchaseRequestBusiness purchaseRequestBusiness,
                                      ShippingAddressBusiness shippingAddressBusiness, UserBusiness userBusiness,
                                      ModelMapper modelMapper) {
        this.purchaseRequestBusiness = purchaseRequestBusiness;
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.userBusiness = userBusiness;
        this.modelMapper = modelMapper;
    }


    @Override
    public PurchaseRequestResponse<List<RequestItemDTO>> createOnlinePurchaseRequest(PurchaseRequestModel purchaseRequestModel) {
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
                purchaseRequest.setRequestType(RequestType.ONLINE);
                purchaseRequest = purchaseRequestBusiness.create(purchaseRequest);

                PurchaseRequestResponse<List<RequestItemDTO>> response = modelMapper.map(purchaseRequest, PurchaseRequestResponse.class);
                List<RequestItemDTO> data = purchaseRequest.getRequestItems().stream()
                        .map(item -> modelMapper.map(item, RequestItemDTO.class))
                        .toList();
                response.setData(data);
                log.debug("createPurchaseRequest() PurchaseRequestServiceImpl end | response : {}", response);
                return response;
            } else {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Người dùng hiện tại không phải là khách hàng");
            }
        } catch (Exception e) {
            log.error("createPurchaseRequest() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public PurchaseRequestResponse<SubRequestDTO> createOfflinePurchaseRequest(PurchaseRequestModel purchaseRequestModel) {
        try {
            log.debug("createOfflinePurchaseRequest() PurchaseRequestServiceImpl start | request : {}", purchaseRequestModel);
            if (purchaseRequestModel.getContactInfo().isEmpty()) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Thông tin liên hệ không được để trống");
            }
            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(purchaseRequestModel.getShippingAddressId()))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy địa chỉ nhận hàng"));

            User user = userBusiness.getById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng hiện tại"));

            if (user instanceof Customer customer) {
                PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                        .shippingAddress(shippingAddress)
                        .customer(customer)
                        .status(PurchaseRequestStatus.SENT)
                        .build();
                SubRequest subRequest = SubRequest.builder()
                        .contactInfo(purchaseRequestModel.getContactInfo())
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
                                .subRequest(subRequest)
                                .build())
                        .toList();
                purchaseRequest.setRequestItems(requestItems);
                purchaseRequest.setRequestType(RequestType.OFFLINE);
                purchaseRequest = purchaseRequestBusiness.create(purchaseRequest);


                PurchaseRequestResponse<SubRequestDTO> response = modelMapper.map(purchaseRequest, PurchaseRequestResponse.class);
                SubRequestDTO subRequestDTO = purchaseRequest.getRequestItems().stream()
                        .filter(item -> item.getSubRequest() != null)
                        .findFirst()
                        .map(item -> modelMapper.map(item.getSubRequest(), SubRequestDTO.class))
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy thông tin yêu cầu phụ"));
                List<RequestItemDTO> listItem = purchaseRequest.getRequestItems().stream()
                        .map(item -> modelMapper.map(item, RequestItemDTO.class))
                        .toList();
                subRequestDTO.setRequestItems(listItem);
                response.setData(subRequestDTO);
                log.debug("createOfflinePurchaseRequest() PurchaseRequestServiceImpl end | response : {}", requestItems);
                return response;
            } else {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Người dùng hiện tại không phải là khách hàng");
            }
        } catch (Exception e) {
            log.error("createOfflinePurchaseRequest() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
        }
    }
}
