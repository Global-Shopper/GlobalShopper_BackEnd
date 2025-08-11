package com.sep490.gshop.service;

import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.CancelModel;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.payload.request.order.CheckOutModel;
import com.sep490.gshop.payload.request.order.DirectCheckoutModel;
import com.sep490.gshop.payload.request.order.ShippingInformationModel;
import com.sep490.gshop.payload.response.PaymentURLResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
        OrderDTO createOrder(OrderRequest orderRequest);
        OrderDTO updateOrder(OrderRequest orderRequest, UUID orderId);
        OrderDTO getOrderById(UUID orderId);
        Page<OrderDTO> getAllOrders(Pageable pageable, OrderStatus status);
        boolean deleteOrder(UUID orderId);
        OrderDTO checkoutOrder(CheckOutModel checkOutModel);
        PaymentURLResponse directCheckoutOrder(DirectCheckoutModel checkOutModel);

        OrderDTO updateShippingInfo(String orderId, ShippingInformationModel shippingInformationModel);

        OrderDTO cancelOrder(UUID orderId, CancelModel cancelModel);
}

