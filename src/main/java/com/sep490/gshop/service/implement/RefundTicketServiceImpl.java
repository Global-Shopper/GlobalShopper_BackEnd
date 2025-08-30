package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.*;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.security.services.UserDetailsImpl;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.RefundReasonDTO;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.CreateReasonRequest;
import com.sep490.gshop.payload.request.refund.ProcessRefundModel;
import com.sep490.gshop.payload.request.refund.RefundTicketRequest;
import com.sep490.gshop.payload.request.refund.RejectRefundModel;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.RefundTicketService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.CalculationUtil;
import jakarta.persistence.EntityNotFoundException;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class RefundTicketServiceImpl implements RefundTicketService {
    private final RefundTicketBusiness refundTicketBusiness;
    private final ModelMapper modelMapper;
    private final OrderBusiness orderBusiness;
    private final WalletBusiness walletBusiness;
    private final TransactionBusiness transactionBusiness;
    private final UserBusiness userBusiness;

    @Autowired
    public RefundTicketServiceImpl(RefundTicketBusiness refundTicketBusiness, ModelMapper modelMapper, OrderBusiness orderBusiness, WalletBusiness walletBusiness, TransactionBusiness transactionBusiness, UserBusiness userBusiness) {
        this.refundTicketBusiness = refundTicketBusiness;
        this.modelMapper = modelMapper;
        this.orderBusiness = orderBusiness;
        this.walletBusiness = walletBusiness;
        this.transactionBusiness = transactionBusiness;
        this.userBusiness = userBusiness;
    }

    @Override
    public RefundTicketDTO createNewRefundTicket(RefundTicketRequest refundTicket) {
        try {
            log.debug("createNewRefundTicket() RefundTicketServiceImpl Start | entity: {}", refundTicket);
            Order order = orderBusiness.getById(UUID.fromString(refundTicket.getOrderId()))
                    .orElseThrow(() -> new AppException(404, "Không tim thấy đơn hàng"));
            RefundTicket newRefundTicket = RefundTicket.builder()
                    .reason(refundTicket.getReason())
                    .evidence(refundTicket.getEvidence())
                    .status(RefundStatus.PENDING)
                    .order(order)
                    .build();
            RefundTicketDTO refundTicketDTO = modelMapper.map(refundTicketBusiness.create(newRefundTicket), RefundTicketDTO.class);
            refundTicketDTO.setOrderId(order.getId().toString());
            log.debug("createNewRefundTicket() RefundTicketServiceImpl End | entity: {}", refundTicketDTO);
            return refundTicketDTO;
        }catch (Exception e) {
            log.error("createNewRefundTicket() RefundTicketServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public RefundTicketDTO getRefundTicketById(UUID id) {
        try {
            log.debug("getRefundTicketById() RefundTicketServiceImpl Start | id: {}", id);
            var entityFound = refundTicketBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy yêu cầu hoàn tiền với id: " + id));
            RefundTicketDTO refundDTO = modelMapper.map(entityFound, RefundTicketDTO.class);
            refundDTO.setOrderId(entityFound.getOrder().getId().toString());
            log.debug("getRefundTicketById() End | RefundTicketDTO: {}", refundDTO);
            return refundDTO;
        } catch (Exception e) {
            log.error("createNewRefundTicket() RefundTicketServiceImpl Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<RefundTicketDTO> getAllRefundTickets(Pageable pageable, RefundStatus status) {
        try {
            log.debug("getAllRefundTickets() RefundTicketServiceImpl Start");
            UserDetailsImpl user = AuthUtils.getCurrentUser();

            Page<RefundTicketDTO> refundList = refundTicketBusiness.getAll(user.getId(), status, pageable)
                    .map(refundTicket ->{
                        RefundTicketDTO dto = modelMapper.map(refundTicket, RefundTicketDTO.class);
                        dto.setOrderId(refundTicket.getOrder().getId().toString());
                        return dto;
                    });

            log.debug("getAllRefundTickets() RefundTicketServiceImpl End | Size: {}", refundList.getSize());
            return refundList;
        } catch (Exception e) {
            log.error("createNewRefundTicket() Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public RefundTicketDTO updateRefundTicket(UUID id, RefundTicketRequest request) {
        try {
            log.debug("updateRefundTicket() RefundTicketServiceImpl Start | id: {}, request: {}", id, request);
            var entityFound = refundTicketBusiness.getById(id)
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy refund ticket").code(404).build());
            entityFound.setEvidence(request.getEvidence());
            entityFound.setReason(request.getReason());
            RefundTicketDTO dto = modelMapper.map(refundTicketBusiness.update(entityFound), RefundTicketDTO.class);
            dto.setOrderId(entityFound.getOrder().getId().toString());
            log.debug("updateRefundTicket() RefundTicketServiceImpl  End | Updated RefundTicketDTO: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createNewRefundTicket() RefundTicketServiceImpl Exception | entity: {}, message: {}", request, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteRefundTicket(UUID id) {
        try {
            log.debug("deleteRefundTicket() RefundTicketServiceImpl Start | id: {}", id);
            boolean result = refundTicketBusiness.delete(id);
            log.debug("deleteRefundTicket() RefundTicketServiceImpl End | id: {}, result: {}", id, result);
            return result;
        } catch (Exception e) {
            log.error("createNewRefundTicket() RefundTicketServiceImpl  Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public RefundTicketDTO  processRefundTicket(ProcessRefundModel processRefundModel, String ticketId) {
        try {
            log.debug("processRefundTicket() RefundTicketServiceImpl Start | ticketId: {}, processRefundModel: {}", ticketId, processRefundModel);
            RefundTicket refundTicket = refundTicketBusiness.getById(UUID.fromString(ticketId))
                    .orElseThrow(() -> new EntityNotFoundException("Refund ticket not found"));
            Order order = refundTicket.getOrder();
            Customer customer = order.getCustomer();
            Wallet wallet = customer.getWallet();
            double amount = Math.round((order.getTotalPrice()+ order.getShippingFee())*processRefundModel.getRefundRate());
            refundTicket.setAmount(amount);
            refundTicket.setStatus(RefundStatus.COMPLETED);
            refundTicket.setRefundRate(processRefundModel.getRefundRate());
            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .balanceBefore(wallet.getBalance())
                    .balanceAfter(wallet.getBalance() + amount)
                    .type(TransactionType.REFUND)
                    .description("Hoàn tiền cho đơn hàng " + order.getId())
                    .status(TransactionStatus.SUCCESS)
                    .customer(customer)
                    .build();
            transactionBusiness.create(transaction);
            wallet.setBalance(wallet.getBalance() + amount);
            walletBusiness.update(wallet);
            RefundTicketDTO dto = modelMapper.map(refundTicketBusiness.update(refundTicket), RefundTicketDTO.class);
            order.setStatus(OrderStatus.CANCELLED);
            OrderHistory orderHistory = new OrderHistory(order,"Đã xác nhận hoàn tiền");
            order.getHistory().add(orderHistory);
            orderBusiness.update(order);
            dto.setOrderId(order.getId().toString());
            log.debug("processRefundTicket() RefundTicketServiceImpl End | Updated RefundTicketDTO: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("processRefundTicket() RefundTicketServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public RefundTicketDTO rejectRefundTicket(String ticketId, RejectRefundModel rejectRefundModel) {
        try {
            log.debug("rejectRefundTicket() RefundTicketServiceImpl Start | ticketId: {}, rejectRefundModel: {}", ticketId, rejectRefundModel);
            RefundTicket refundTicket = refundTicketBusiness.getById(UUID.fromString(ticketId))
                    .orElseThrow(() -> new AppException(404,"Không tìm thấy yêu cầu hoàn tiền với id: " + ticketId));
            refundTicket.setStatus(RefundStatus.REJECTED);
            refundTicket.setRejectionReason(rejectRefundModel.getRejectionReason());
            RefundTicketDTO dto = modelMapper.map(refundTicketBusiness.update(refundTicket), RefundTicketDTO.class);
            dto.setOrderId(refundTicket.getOrder().getId().toString());
            log.debug("rejectRefundTicket() RefundTicketServiceImpl End | Updated RefundTicketDTO: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("rejectRefundTicket() RefundTicketServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public RefundTicketDTO getTicketByOrderId(String orderId) {
        log.debug("getTicketByOrderId() START | orderId: {}", orderId);

        try {
            UUID currentUserId = AuthUtils.getCurrentUserId();
            var currentUser = userBusiness.getById(currentUserId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Vui lòng đăng nhập để tiếp tục")
                            .code(401)
                            .build());

            var order = orderBusiness.getById(UUID.fromString(orderId))
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy order")
                            .code(404)
                            .build());

            if (currentUser.getRole() == UserRole.CUSTOMER) {
                if (!order.getCustomer().getId().equals(currentUserId)) {
                    throw AppException.builder()
                            .message("Bạn không có quyền truy cập tài nguyên này")
                            .code(403)
                            .build();
                }
            }

            if (currentUser.getRole() == UserRole.ADMIN) {
                if (!order.getAdmin().getId().equals(currentUserId)) {
                    throw AppException.builder()
                            .message("Bạn không có quyền truy cập tài nguyên này")
                            .code(403)
                            .build();
                }
            }

            var refundTicket = refundTicketBusiness.getRefundTicketByOrderId(order.getId());
            if (refundTicket == null) {
                AppException.builder()
                        .message("Không tìm thấy refund ticket cho order này")
                        .code(404)
                        .build();
            }

            var refundTicketDTO = modelMapper.map(refundTicket, RefundTicketDTO.class);

            log.debug("getTicketByOrderId() SUCCESS | orderId: {}", orderId);
            return refundTicketDTO;

        } catch (Exception e) {
            log.error("getTicketByOrderId() ERROR | orderId: {}, error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RefundReasonDTO> getRefundReasons() {
        try {
            log.debug("getRefundReasons() RefundTicketServiceImpl Start");
            List<RefundReasonDTO> reasons = refundTicketBusiness.getAllRefundReasons()
                    .stream()
                    .map(reason -> modelMapper.map(reason, RefundReasonDTO.class))
                    .toList();
            log.debug("getRefundReasons() RefundTicketServiceImpl End | Size: {}", reasons.size());
            return reasons;
        } catch (Exception e) {
            log.error("getRefundReasons() RefundTicketServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public RefundReasonDTO createRefundReason(CreateReasonRequest reason) {
        try {
            log.debug("createRefundReason() RefundTicketServiceImpl Start | reason: {}", reason);
            RefundReason newReason = new RefundReason(reason.getReason(), reason.getRate(), true);
            RefundReasonDTO dto = modelMapper.map(refundTicketBusiness.createRefundReason(newReason), RefundReasonDTO.class);
            log.debug("createRefundReason() RefundTicketServiceImpl End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createRefundReason() RefundTicketServiceImpl Exception | entity: {}, message: {}", reason, e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse deleteReason(String id) {
        try {
            log.debug("deleteReason() RefundTicketServiceImpl Start | id: {}", id);

            refundTicketBusiness.deleteReasonById(UUID.fromString(id));
            log.debug("deleteReason() RefundTicketServiceImpl End | id: {}", id);
            return MessageResponse.builder()
                    .message("Xoá lí do hoàn tiền thành công")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("deleteReason() RefundTicketServiceImpl  Exception | id: {}, message: {}", id, e.getMessage());
            return MessageResponse.builder()
                    .message("Xoá lí do hoàn tiền thất bại: " + e.getMessage())
                    .isSuccess(false)
                    .build();
        }
    }

    @Override
    public RefundReasonDTO changeIsActive(String id) {
        try {
            log.debug("changeIsActive() RefundTicketServiceImpl Start | id: {}", id);
            RefundReason reason = refundTicketBusiness.getReasonById(UUID.fromString(id));
            reason.setActive(!reason.getActive());
            RefundReasonDTO dto = modelMapper.map(refundTicketBusiness.updateRefundReason(reason), RefundReasonDTO.class);
            log.debug("changeIsActive() RefundTicketServiceImpl End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("changeIsActive() RefundTicketServiceImpl Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }
}
