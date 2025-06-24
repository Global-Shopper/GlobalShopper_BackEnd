package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.business.RefundTicketBusiness;
import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.entity.RefundTicket;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.request.RefundTicketRequest;
import com.sep490.gshop.service.RefundTicketService;
import jakarta.persistence.EntityNotFoundException;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class RefundTicketServiceImpl implements RefundTicketService {
    private RefundTicketBusiness refundTicketBusiness;
    private ModelMapper modelMapper;
    private OrderBusiness orderBusiness;

    @Autowired
    public RefundTicketServiceImpl(RefundTicketBusiness refundTicketBusiness, ModelMapper modelMapper, OrderBusiness orderBusiness) {
        this.refundTicketBusiness = refundTicketBusiness;
        this.modelMapper = modelMapper;
        this.orderBusiness = orderBusiness;
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
            log.error("createNewRefundTicket() Exception | entity: {}, message: {}", refundTicket, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public RefundTicketDTO createNewRefundTicket(RefundTicketRequest refundTicket) {
        try {
            log.debug("createNewRefundTicket() Start | entity: {}", refundTicket);
            var newRefundTicket = modelMapper.map(refundTicket, RefundTicket.class);
            newRefundTicket.setId(UUID.randomUUID());
            newRefundTicket.setStatus(RefundStatus.PENDING);
            newRefundTicket.setOrder(orderBusiness.getById(UUID.fromString(refundTicket.getOrderId())).orElseThrow(() -> new AppException(404, "Order not found")));
            log.debug("createNewRefundTicket() End | entity: {}", refundTicket);
            return modelMapper.map(refundTicketBusiness.create(newRefundTicket), RefundTicketDTO.class);
        }catch (Exception e) {
            log.error("createNewRefundTicket() Exception | entity: {}, message: {}", refundTicket, e.getMessage(), e);
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
            log.error("createNewRefundTicket() Exception | id: {}, message: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<RefundTicketDTO> getAllRefundTickets() {
        try {
            log.debug("getAllRefundTickets() Start");
            var entitysList = refundTicketBusiness.getAll().stream()
                    .map(refundTicket -> modelMapper.map(refundTicket, RefundTicketDTO.class))
                    .toList();
            log.debug("getAllRefundTickets() End | Size: {}", entitysList.size());
            return entitysList;
        } catch (Exception e) {
            log.error("createNewRefundTicket() Exception | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public RefundTicketDTO updateRefundTicket(UUID id, RefundTicketRequest request) {
        try {
            log.debug("updateRefundTicket() Start | id: {}, request: {}", id, request);
            var entityFound = refundTicketBusiness.getById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Refund ticket not found"));
            entityFound.setEvidence(request.getEvidence());
            entityFound.setReason(request.getReason());
            RefundTicketDTO dto = modelMapper.map(refundTicketBusiness.update(entityFound), RefundTicketDTO.class);
            log.debug("updateRefundTicket() End | Updated RefundTicketDTO: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createNewRefundTicket() Exception | entity: {}, message: {}", request, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteRefundTicket(UUID id) {
        try {
            log.debug("deleteRefundTicket() Start | id: {}", id);
            boolean result = refundTicketBusiness.delete(id);
            log.debug("deleteRefundTicket() End | id: {}, result: {}", id, result);
            return result;
        } catch (Exception e) {
            log.error("createNewRefundTicket() Exception | id: {}, message: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
