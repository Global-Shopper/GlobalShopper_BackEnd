package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.PurchaseRequestDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.purchaserequest.OfflineRequest;
import com.sep490.gshop.payload.request.purchaserequest.OnlineRequest;
import com.sep490.gshop.payload.request.purchaserequest.SubRequestModel;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import com.sep490.gshop.service.PurchaseRequestService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestBusiness purchaseRequestBusiness;
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final RequestItemBusiness requestItemBusiness;
    private final SubRequestBusiness subRequestBusiness;
    private final UserBusiness userBusiness;
    private final ModelMapper modelMapper;

    @Autowired
    public PurchaseRequestServiceImpl(PurchaseRequestBusiness purchaseRequestBusiness,
                                      ShippingAddressBusiness shippingAddressBusiness, RequestItemBusiness requestItemBusiness, SubRequestBusiness subRequestBusiness, UserBusiness userBusiness,
                                      ModelMapper modelMapper) {
        this.purchaseRequestBusiness = purchaseRequestBusiness;
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.userBusiness = userBusiness;
        this.modelMapper = modelMapper;
    }


    @Override
    public PurchaseRequestResponse<List<RequestItemDTO>> createOnlinePurchaseRequest(OnlineRequest onlineRequest) {
        try {
            log.debug("createPurchaseRequest() PurchaseRequestServiceImpl start | request : {}", onlineRequest);
            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(onlineRequest.getShippingAddressId()))
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
                List<RequestItem> requestItems = onlineRequest.getItems().stream()
                        .map(item -> RequestItem.builder()
                                .productName(item.getName())
                                .purchaseRequest(finalPurchaseRequest)
                                .productURL(item.getLink())
                                .quantity(item.getQuantity())
                                .description(item.getNote())
                                .variants(item.getVariants())
                                .images(item.getImages())
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
    public PurchaseRequestResponse<SubRequestDTO> createOfflinePurchaseRequest(OfflineRequest offlineRequest) {
        try {
            log.debug("createOfflinePurchaseRequest() PurchaseRequestServiceImpl start | request : {}", offlineRequest);
            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(offlineRequest.getShippingAddressId()))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy địa chỉ nhận hàng"));
            User user = userBusiness.getById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng hiện tại"));

            if (user instanceof Customer customer) {
                PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                        .shippingAddress(shippingAddress)
                        .customer(customer)
                        .status(PurchaseRequestStatus.SENT)
                        .build();
                SubRequest subRequest = SubRequest.builder()
                        .contactInfo(offlineRequest.getContactInfo())
                        .build();
                purchaseRequest.setAdmin(null);
                purchaseRequest.setExpiredAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                PurchaseRequest finalPurchaseRequest = purchaseRequest;
                List<RequestItem> requestItems = offlineRequest.getItems().stream()
                        .map(item -> RequestItem.builder()
                                .productName(item.getName())
                                .purchaseRequest(finalPurchaseRequest)
                                .productURL(item.getLink())
                                .quantity(item.getQuantity())
                                .description(item.getNote())
                                .variants(item.getVariants())
                                .images(item.getImages())
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

    @Override
    public MessageResponse checkPurchaseRequest(String id) {
        try {
            log.debug("checkPurchaseRequest() PurchaseRequestServiceImpl start | id : {}", id);
            PurchaseRequest purchaseRequest = purchaseRequestBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy yêu cầu mua hàng"));
            boolean checkStatus = purchaseRequest.getStatus() == PurchaseRequestStatus.SENT;
            if (!checkStatus) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Chỉ có thể chuyển trạng thái yêu cầu mua hàng từ Đã gửi sang Đang kiểm tra");
            }
            purchaseRequest.setStatus(PurchaseRequestStatus.CHECKING);
            Admin admin = (Admin) userBusiness.getById(AuthUtils.getCurrentUserId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng hiện tại"));
            purchaseRequest.setAdmin(admin);
            purchaseRequest = purchaseRequestBusiness.update(purchaseRequest);
            if (purchaseRequest.getAdmin().getId() == AuthUtils.getCurrentUserId() && purchaseRequest.getStatus() == PurchaseRequestStatus.CHECKING) {
                log.debug("checkPurchaseRequest() PurchaseRequestServiceImpl end | isSuccess : true");
                return new MessageResponse("Chuyển trạng thái thành công", true);
            }
            log.debug("checkPurchaseRequest() PurchaseRequestServiceImpl end | isSuccess : false");
            return new MessageResponse("Chuyển trạng thái không thành công", false);
        } catch (Exception e) {
            log.error("checkPurchaseRequest() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<PurchaseRequestDTO> getPurchaseRequests(int page, int size, Sort.Direction direction, String type) {
        try {
            log.debug("getPurchaseRequests() start | page: {}, size: {}, type: {}", page, size, type);
            Sort sort = Sort.by(direction, "createdAt");
            Pageable pageable = PageRequest.of(page, size,sort);
            UserRole role = AuthUtils.getCurrentUser().getRole();
            UUID userId = AuthUtils.getCurrentUserId();
            Page<PurchaseRequest> purchaseRequests = null;
            if (UserRole.CUSTOMER.equals(role)) {
                purchaseRequests = purchaseRequestBusiness.findByCustomerId(userId, pageable);
            } else if (UserRole.ADMIN.equals(role)) {
                purchaseRequests = switch (type != null ? type.toLowerCase() : "") {
                    case "unassigned" -> purchaseRequestBusiness.findUnassignedRequests(pageable);
                    case "assigned" -> purchaseRequestBusiness.findAssignedRequestsByAdminId(userId, pageable);
                    default ->
                            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Sai loại Yêu cầu. Sử dụng 'Chưa được nhận' or 'Đã được nhận'.");
                };
            }
            if (purchaseRequests == null || purchaseRequests.isEmpty()) {
                log.debug("getPurchaseRequests() end | response: empty");
                return Page.empty();
            }
            Page<PurchaseRequestDTO> response = purchaseRequests.map(purchaseRequest -> {
                PurchaseRequestDTO dto = modelMapper.map(purchaseRequest, PurchaseRequestDTO.class);
                List<RequestItemDTO> requestItems = purchaseRequest.getRequestItems().stream()
                        .map(item -> modelMapper.map(item, RequestItemDTO.class))
                        .toList();
                dto.setRequestItems(requestItems);
                return dto;
            });
            log.debug("getPurchaseRequests() end | response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("getPurchaseRequests() error | message : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MessageResponse createSubRequest(SubRequestModel subRequestModel) {
        try {
            log.debug("createSubRequest() PurchaseRequestServiceImpl start | subRequestModel: {}", subRequestModel);
            List<RequestItem> items = requestItemBusiness.findAllById(subRequestModel.getItemIds());
            if (items.isEmpty()) {
                throw new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy yêu cầu mua hàng nào với ID đã cho");
            }
            UUID purchaseRequestId = items.get(0).getPurchaseRequest().getId();
            if (items.stream().anyMatch(i -> !i.getPurchaseRequest().getId().equals(purchaseRequestId))) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Tất cả các sản phầm phải thuộc cùng một yêu cầu mua hàng");
            }
            if (!AuthUtils.getCurrentUserId().equals(items.get(0).getPurchaseRequest().getAdmin().getId())) {
                throw new AppException(HttpStatus.FORBIDDEN.value(), "Người dùng hiện tại không phải là quản trị viên của yêu cầu mua hàng này");
            }
            if (items.stream().anyMatch(i -> i.getSubRequest() != null)) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Một hoặc nhiều sản phẩm đã có nhóm");
            }
            SubRequest subRequest = subRequestBusiness.create(SubRequest.builder()
                    .contactInfo(subRequestModel.getContactInfo())
                    .seller(subRequestModel.getSeller())
                    .ecommercePlatform(subRequestModel.getEcommercePlatform())
                    .build());
            items.forEach(item -> item.setSubRequest(subRequest));
            List<RequestItem> savedItem = requestItemBusiness.saveAll(items);
            if (savedItem.isEmpty() || savedItem.size() != items.size()) {
                throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Không thể tạo nhóm yêu cầu");
            }
            return new MessageResponse("Tạo nhóm yêu cầu thành công", true);
        } catch (Exception e) {
            log.error("createSubRequest() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
        }
    }
}
