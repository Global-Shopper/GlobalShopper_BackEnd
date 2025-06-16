package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.OrderRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
        OrderDTO createOrder(OrderRequest orderRequest);
        OrderDTO updateOrder(OrderRequest orderRequest, UUID orderId);
        OrderDTO getOrderById(UUID orderId);
        List<OrderDTO> getAllOrders();
        boolean deleteOrder(UUID orderId);
}

