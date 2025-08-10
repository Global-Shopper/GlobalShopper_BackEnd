package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.RequestItemBusiness;
import com.sep490.gshop.business.SubRequestBusiness;
import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.RequestItem;
import com.sep490.gshop.payload.dto.SubRequestDTO;
import com.sep490.gshop.payload.request.SubUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.SubRequestService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Log4j2
public class SubRequestServiceImpl implements SubRequestService
{
    private SubRequestBusiness subRequestBusiness;
    private RequestItemBusiness requestItemBusiness;
    private ModelMapper modelMapper;
    public SubRequestServiceImpl(SubRequestBusiness subRequestBusiness, RequestItemBusiness requestItemBusiness, ModelMapper modelMapper){
        this.subRequestBusiness = subRequestBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.modelMapper = modelMapper;

    }
    @Override
    public MessageResponse removeRequestItem(UUID subRequestId, UUID itemId) {
        log.debug("removeRequestItem() start | subRequestId: {}, itemId: {}", subRequestId, itemId);
        try {
            var currentUserId = AuthUtils.getCurrentUserId();

            var subRequest = subRequestBusiness.getById(subRequestId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy sub request")
                            .code(404)
                            .build());

            var requestItem = requestItemBusiness.getById(itemId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy request item")
                            .code(404)
                            .build());

            var purchaseRequest = requestItem.getPurchaseRequest();
            if (purchaseRequest.getAdmin() == null || !purchaseRequest.getAdmin().getId().equals(currentUserId)) {
                throw AppException.builder()
                        .message("Bạn không có quyền xoá item này")
                        .code(403)
                        .build();
            }

            if (subRequest.getRequestItems().stream().noneMatch(i -> i.getId().equals(itemId))) {
                throw AppException.builder()
                        .message("Request item không thuộc về sub request này")
                        .code(400)
                        .build();
            }
            if(subRequest.getStatus().equals(SubRequestStatus.QUOTED)){
                throw AppException.builder().message("Sub request này đã được báo giá, không thể chỉnh sửa").code(400).build();
            }
            subRequest.getRequestItems().removeIf(i -> i.getId().equals(itemId));
            requestItem.setSubRequest(null);

            requestItemBusiness.update(requestItem);
            subRequestBusiness.update(subRequest);

            if (subRequest.getRequestItems().isEmpty()) {
                subRequestBusiness.delete(subRequest.getId());
                log.debug("removeRequestItem() end | Deleted item {} and removed empty subRequest {}", itemId, subRequestId);
                return MessageResponse.builder()
                        .message("Xoá thành công item và sub request không còn item nào nên đã bị xoá luôn")
                        .isSuccess(true)
                        .build();
            }

            log.debug("removeRequestItem() end | Deleted item {} from subRequest {}", itemId, subRequestId);
            return MessageResponse.builder()
                    .message("Xoá thành công item khỏi sub request")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("removeRequestItem() error | subRequestId: {}, itemId: {}, message: {}", subRequestId, itemId, e.getMessage());
            throw e;
        }
    }



    @Override
    public MessageResponse addRequestItem(UUID subRequestId, UUID itemId) {
        log.debug("addRequestItem() start | subRequestId: {}, itemId: {}", subRequestId, itemId);
        try {
            var currentUserId = AuthUtils.getCurrentUserId();

            var subRequest = subRequestBusiness.getById(subRequestId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy sub request")
                            .code(404)
                            .build());

            var requestItem = requestItemBusiness.getById(itemId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy request item")
                            .code(404)
                            .build());

            var purchaseRequest = requestItem.getPurchaseRequest();
            if (purchaseRequest.getAdmin() == null || !purchaseRequest.getAdmin().getId().equals(currentUserId)) {
                throw AppException.builder()
                        .message("Bạn không có quyền thêm item vào sub request này")
                        .code(403)
                        .build();
            }

            if (requestItem.getSubRequest() != null) {
                throw AppException.builder()
                        .message("Request item này đã thuộc về một sub request khác")
                        .code(400)
                        .build();
            }

            if(subRequest.getRequestItems().stream().anyMatch(i -> i.getId().equals(requestItem.getId()))) {
                throw AppException.builder().message("Request item đã thuộc về sub request này").code(400).build();
            }

            if(subRequest.getStatus().equals(SubRequestStatus.QUOTED)){
                throw AppException.builder().message("Sub request này đã được báo giá, không thể chỉnh sửa").code(400).build();
            }
            subRequest.getRequestItems().add(requestItem);
            requestItem.setSubRequest(subRequest);

            requestItemBusiness.update(requestItem);
            subRequestBusiness.update(subRequest);

            log.debug("addRequestItem() end | Added item {} to subRequest {}", itemId, subRequestId);
            return MessageResponse.builder()
                    .message("Thêm thành công item vào sub request")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("addRequestItem() error | subRequestId: {}, itemId: {}, message: {}", subRequestId, itemId, e.getMessage());
            throw e;
        }
    }


    @Override
    public SubRequestDTO updateSubRequest(UUID subRequestId, SubUpdateRequest subUpdateRequest) {
        log.debug("updateSubRequest() start | subRequestId: {}, updateData: {}", subRequestId, subUpdateRequest);
        try {
            var subRequest = subRequestBusiness.getById(subRequestId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy sub request")
                            .code(404)
                            .build());

            var currentUserId = AuthUtils.getCurrentUserId();
            var purchaseRequest = subRequest.getRequestItems().isEmpty() ? null
                    : subRequest.getRequestItems().get(0).getPurchaseRequest();
            if (purchaseRequest != null &&
                    (purchaseRequest.getAdmin() == null || !purchaseRequest.getAdmin().getId().equals(currentUserId))) {
                throw AppException.builder()
                        .message("Bạn không có quyền cập nhật sub request này")
                        .code(403)
                        .build();
            }
            if(subRequest.getStatus().equals(SubRequestStatus.QUOTED)){
                throw AppException.builder().message("Sub request này đã được báo giá, không thể chỉnh sửa").code(400).build();
            }
            subRequest.setSeller(subUpdateRequest.getSeller());
            subRequest.setEcommercePlatform(subUpdateRequest.getEcommercePlatform());

            subRequestBusiness.update(subRequest);

            SubRequestDTO response = modelMapper.map(subRequest, SubRequestDTO.class);

            log.debug("updateSubRequest() end | subRequestId: {}", subRequestId);
            return response;
        } catch (Exception e) {
            log.error("updateSubRequest() error | subRequestId: {}, message: {}", subRequestId, e.getMessage());
            throw e;
        }
    }

}
