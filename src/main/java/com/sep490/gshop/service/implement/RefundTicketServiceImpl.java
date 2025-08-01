package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.BankAccountBusiness;
import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.business.RefundTicketBusiness;
import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.security.services.UserDetailsImpl;
import com.sep490.gshop.entity.BankAccount;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.entity.subclass.BankAccountSnapshot;
import com.sep490.gshop.payload.dto.BankAccountDTO;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.RefundTicketRequest;
import com.sep490.gshop.service.RefundTicketService;
import com.sep490.gshop.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class RefundTicketServiceImpl implements RefundTicketService {
    private final RefundTicketBusiness refundTicketBusiness;
    private final ModelMapper modelMapper;
    private final OrderBusiness orderBusiness;
    private final BankAccountBusiness bankAccountBusiness;

    @Autowired
    public RefundTicketServiceImpl(RefundTicketBusiness refundTicketBusiness, ModelMapper modelMapper, OrderBusiness orderBusiness, BankAccountBusiness bankAccountBusiness) {
        this.refundTicketBusiness = refundTicketBusiness;
        this.modelMapper = modelMapper;
        this.orderBusiness = orderBusiness;
        this.bankAccountBusiness = bankAccountBusiness;
    }

    @Override
    public RefundTicketDTO createNewRefundTicketAssignToOrder(RefundTicketRequest refundTicket, UUID orderId) {
        try {
           Optional<Order> orderExist = orderBusiness.getById(orderId);
           if (orderExist.isEmpty()) {
               new EntityNotFoundException("Order not found");
           }
           if(refundTicketBusiness.getRefundTicketByOrderId(orderId) != null) {
               throw new EntityNotFoundException("Refund ticket already exist");
           }
           RefundTicket newRefundTicket = modelMapper.map(refundTicket, RefundTicket.class);
           newRefundTicket.setId(UUID.randomUUID());
           newRefundTicket.setOrder(orderExist.orElseThrow(() -> new AppException(404, "Order not found")));
           newRefundTicket.setStatus(RefundStatus.PENDING);
           RefundTicketDTO createdTicket = modelMapper.map(refundTicketBusiness.create(newRefundTicket), RefundTicketDTO.class);
           return createdTicket;
        } catch (Exception e) {
            log.error("createNewRefundTicket() Exception | entity: {}, message: {}", refundTicket, e.getMessage());
            throw e;
        }
    }

    @Override
    public RefundTicketDTO createNewRefundTicket(RefundTicketRequest refundTicket) {
        try {
            log.debug("createNewRefundTicket() Start | entity: {}", refundTicket);
            Order order = orderBusiness.getById(UUID.fromString(refundTicket.getOrderId()))
                    .orElseThrow(() -> new AppException(404, "Không tim thấy đơn hàng"));
            BankAccount bankAccount = bankAccountBusiness.getById(UUID.fromString(refundTicket.getBankAccountId()))
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy tài khoản ngân hàng"));
            BankAccountSnapshot bankAccountSnapshot = new BankAccountSnapshot(bankAccount);
            RefundTicket newRefundTicket = RefundTicket.builder()
                    .reason(refundTicket.getReason())
                    .evidence(refundTicket.getEvidence())
                    .status(RefundStatus.PENDING)
                    .order(order)
                    .bankAccount(bankAccountSnapshot)
                    .build();
            RefundTicketDTO refundTicketDTO = modelMapper.map(refundTicketBusiness.create(newRefundTicket), RefundTicketDTO.class);
            log.debug("createNewRefundTicket() End | entity: {}", refundTicketDTO);
            return refundTicketDTO;
        }catch (Exception e) {
            log.error("createNewRefundTicket() RefundTicketServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public RefundTicketDTO getRefundTicketById(UUID id) {
        try {
            log.debug("getRefundTicketById() Start | id: {}", id);
            var entityFound = refundTicketBusiness.getById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Refund ticket not found"));
            RefundTicketDTO refundDTO = modelMapper.map(entityFound, RefundTicketDTO.class);
            log.debug("getRefundTicketById() End | RefundTicketDTO: {}", refundDTO);
            return refundDTO;
        } catch (Exception e) {
            log.error("createNewRefundTicket() Exception | id: {}, message: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<RefundTicketDTO> getAllRefundTickets(Pageable pageable, RefundStatus status) {
        try {
            log.debug("getAllRefundTickets() RefundTicketServiceImpl Start");
            UserDetailsImpl user = AuthUtils.getCurrentUser();

            Page<RefundTicketDTO> refundList = refundTicketBusiness.getAll(user.getId(), status, pageable)
                    .map(refundTicket -> modelMapper.map(refundTicket, RefundTicketDTO.class));

            var entitysList = refundTicketBusiness.getAll().stream()
                    .map(refundTicket -> modelMapper.map(refundTicket, RefundTicketDTO.class))
                    .toList();
            log.debug("getAllRefundTickets() RefundTicketServiceImpl End | Size: {}", entitysList.size());
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
                    .orElseThrow(() -> new EntityNotFoundException("Refund ticket not found"));
            entityFound.setEvidence(request.getEvidence());
            entityFound.setReason(request.getReason());
            RefundTicketDTO dto = modelMapper.map(refundTicketBusiness.update(entityFound), RefundTicketDTO.class);
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
}
