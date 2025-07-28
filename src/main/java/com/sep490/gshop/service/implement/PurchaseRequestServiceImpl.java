package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.security.services.UserDetailsImpl;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.QuotationDetailDTO;
import com.sep490.gshop.payload.dto.RequestItemDTO;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.purchaserequest.*;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.PurchaseRequestModel;
import com.sep490.gshop.payload.response.PurchaseRequestResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.PurchaseRequestService;
import com.sep490.gshop.service.TaxRateService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestBusiness purchaseRequestBusiness;
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final RequestItemBusiness requestItemBusiness;
    private final SubRequestBusiness subRequestBusiness;
    private final UserBusiness userBusiness;
    private final ModelMapper modelMapper;
    private TaxRateService taxRateService;
    @Autowired
    public PurchaseRequestServiceImpl(PurchaseRequestBusiness purchaseRequestBusiness,
                                      ShippingAddressBusiness shippingAddressBusiness, RequestItemBusiness requestItemBusiness, SubRequestBusiness subRequestBusiness, UserBusiness userBusiness,
                                      ModelMapper modelMapper, TaxRateService taxRateService) {
        this.purchaseRequestBusiness = purchaseRequestBusiness;
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.userBusiness = userBusiness;
        this.modelMapper = modelMapper;
        this.taxRateService = taxRateService;
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
                List<RequestItem> requestItems = onlineRequest.getRequestItems().stream()
                        .map(item -> RequestItem.builder()
                                .productName(item.getProductName())
                                .purchaseRequest(finalPurchaseRequest)
                                .productURL(item.getProductURL())
                                .quantity(item.getQuantity())
                                .description(item.getDescription())
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
                List<RequestItem> requestItems = offlineRequest.getRequestItems().stream()
                        .map(item -> RequestItem.builder()
                                .productName(item.getProductName())
                                .purchaseRequest(finalPurchaseRequest)
                                .productURL(item.getProductURL())
                                .quantity(item.getQuantity())
                                .description(item.getDescription())
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
    public Page<PurchaseRequestModel> getPurchaseRequests(int page, int size, Sort.Direction direction, String type) {
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
            Page<PurchaseRequestModel> response = purchaseRequests.map(this::convertToPurchaseRequestModel);
            log.debug("getPurchaseRequests() end | response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("getPurchaseRequests() error | message : {}", e.getMessage());
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

    @Override
    @Transactional
    public MessageResponse updatePurchaseRequest(String id, UpdateRequestModel updateRequestModel) {
        try {
            log.debug("updatePurchaseRequest() PurchaseRequestServiceImpl start | id: {}, updateRequestModel: {}", id, updateRequestModel);
            //Authentication and authorization
            PurchaseRequest purchaseRequest = purchaseRequestBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy yêu cầu mua hàng để cập nhật"));
            if (purchaseRequest.getStatus() != PurchaseRequestStatus.SENT && purchaseRequest.getStatus() != PurchaseRequestStatus.INSUFFICIENT) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Yêu cầu mua hàng không thể cập nhật khi không ở trạng thái Đã gửi");
            }
            UserDetailsImpl user = AuthUtils.getCurrentUser();
            if ((UserRole.CUSTOMER.equals(user.getRole()) && !AuthUtils.getCurrentUserId().equals(purchaseRequest.getCustomer().getId())) ||
                (UserRole.ADMIN.equals(user.getRole()) && !AuthUtils.getCurrentUserId().equals(purchaseRequest.getAdmin().getId()))) {
                throw new AppException(HttpStatus.FORBIDDEN.value(), "Người dùng hiện tại không có quyền cập nhật yêu cầu mua hàng này");
            }

            // Validate shipping address
            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(updateRequestModel.getShippingAddressId()))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy địa chỉ nhận hàng để cập nhật"));

            //Update subRequest contact info if request type is OFFLINE
            SubRequest subRequest = purchaseRequest.getRequestItems().stream()
                    .filter(item -> item.getSubRequest() != null)
                    .findFirst()
                    .map(RequestItem::getSubRequest)
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy nhóm yêu cầu để cập nhật"));
            if (RequestType.OFFLINE.equals(purchaseRequest.getRequestType()) && updateRequestModel.getContactInfo() != null){
                subRequest.setContactInfo(updateRequestModel.getContactInfo());
                subRequestBusiness.update(subRequest);
            }

            // Validate request items
            List<RequestItem> requestItems = purchaseRequest.getRequestItems();

            Map<UUID, RequestItem> itemMap = requestItems.stream()
                    .collect(Collectors.toMap(RequestItem::getId, i -> i));

            List<RequestItem> finalItemList = new ArrayList<>();

            for (UpdateRequestItemModel item : updateRequestModel.getItems()) {
                if (item.getId() == null ) {
                    RequestItem requestItem = RequestItem.builder()
                            .productName(item.getProductName())
                            .productURL(item.getProductURL())
                            .quantity(item.getQuantity())
                            .description(item.getDescription())
                            .variants(item.getVariants())
                            .images(item.getImages())
                            .purchaseRequest(purchaseRequest)
                            .build();
                    if (RequestType.OFFLINE.equals(purchaseRequest.getRequestType())) {
                        requestItem.setSubRequest(subRequest);
                    }
                    finalItemList.add(requestItem);
                } else if (itemMap.containsKey(UUID.fromString(item.getId()))) {
                    RequestItem requestItem = itemMap.get(UUID.fromString(item.getId()));
                    requestItem.setProductName(item.getProductName());
                    requestItem.setProductURL(item.getProductURL());
                    requestItem.setQuantity(item.getQuantity());
                    requestItem.setDescription(item.getDescription());
                    requestItem.setVariants(item.getVariants());
                    requestItem.setImages(item.getImages());
                    finalItemList.add(requestItem);
                } else {
                    throw new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy sản phẩm với ID: " + item.getId());
                }

            }
            for (RequestItem item : requestItems) {
                if (!finalItemList.contains(item)) {
                    requestItemBusiness.delete(item.getId());
                }
            }
            purchaseRequest.setShippingAddress(shippingAddress);
            purchaseRequest.setRequestItems(finalItemList);
            purchaseRequest = purchaseRequestBusiness.update(purchaseRequest);
            log.debug("updatePurchaseRequest() PurchaseRequestServiceImpl end | isSuccess : true, purchaseRequest: {}", purchaseRequest.getId());
            return new MessageResponse("Cập nhật yêu cầu mua hàng thành công", true);
        } catch (Exception e) {
            log.error("updatePurchaseRequest() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public PurchaseRequestModel getPurchaseRequestById(String id) {
        try {
            log.debug("getPurchaseRequestById() PurchaseRequestServiceImpl start | id: {}", id);
            PurchaseRequest purchaseRequest = purchaseRequestBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy yêu cầu mua hàng với ID: " + id));
            if (!AuthUtils.getCurrentUserId().equals(purchaseRequest.getCustomer().getId())
                    && AuthUtils.getCurrentUser().getRole() != UserRole.ADMIN) {
                throw new AppException(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xem yêu cầu mua hàng này");
            }

            PurchaseRequestModel response = convertToPurchaseRequestModel(purchaseRequest);


            log.debug("getPurchaseRequestById() PurchaseRequestServiceImpl end | response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("getPurchaseRequestById() PurchaseRequestServiceImpl error | message : {}", e.getMessage());
            throw e;
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

    private PurchaseRequestModel convertToPurchaseRequestModel(PurchaseRequest purchaseRequest) {
        List<RequestItem> allItems = purchaseRequest.getRequestItems();
    int count = 0;
        // RequestItems without SubRequest
        List<RequestItemDTO> itemsWithoutSub = allItems.stream()
                .filter(item -> item.getSubRequest() == null)
                .map(item -> {
                    RequestItemDTO dto = modelMapper.map(item, RequestItemDTO.class);
                    // Lấy và set QuotationDetailDTO nếu có
                    if (item.getQuotationDetail() != null) {
                        dto.setQuotationDetail(enrichQuotationDetailDto(item.getQuotationDetail()));
                    }
                    return dto;
                })
                .toList();

        // Group by SubRequest
        Map<SubRequest, List<RequestItem>> subRequestMap = allItems.stream()
                .filter(item -> item.getSubRequest() != null)
                .collect(Collectors.groupingBy(RequestItem::getSubRequest));

        List<SubRequestDTO> subRequestModels = subRequestMap.entrySet().stream()
                .map(entry -> {
                    SubRequestDTO subDTO = modelMapper.map(entry.getKey(), SubRequestDTO.class);
                    List<RequestItemDTO> requestItemDTOs = entry.getValue().stream()
                            .map(item -> {
                                RequestItemDTO dto = modelMapper.map(item, RequestItemDTO.class);
                                if (item.getQuotationDetail() != null) {
                                    dto.setQuotationDetail(enrichQuotationDetailDto(item.getQuotationDetail()));
                                }
                                return dto;
                            })
                            .toList();
                    subDTO.setRequestItems(requestItemDTOs);
                    return subDTO;
                })
                .toList();

        PurchaseRequestModel response = modelMapper.map(purchaseRequest, PurchaseRequestModel.class);
        response.setRequestItems(itemsWithoutSub);
        response.setSubRequests(subRequestModels);
        response.setIsQuotationCount(subRequestMap.size());
        return response;
    }


}
