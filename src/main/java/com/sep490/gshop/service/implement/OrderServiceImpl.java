package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    private OrderBusiness orderBusiness;
    private ModelMapper modelMapper;
    @Autowired
    public OrderServiceImpl(OrderBusiness orderBusiness, ModelMapper modelMapper) {
        this.orderBusiness = orderBusiness;
        this.modelMapper = modelMapper;
    }
    @Override
    public OrderDTO FindById(UUID id) {
        log.debug("FindById() OrderServiceImpl Start | id: {}", id);
        var foundEntity = orderBusiness.getById(id).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        log.debug("FindById() OrderServiceImpl End | Customer found: {}", foundEntity);
        return modelMapper.map(foundEntity, OrderDTO.class);
    }
}
