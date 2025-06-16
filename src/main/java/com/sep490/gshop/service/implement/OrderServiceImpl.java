package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.OrderStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.AddressSnapshot;
import com.sep490.gshop.entity.subclass.ProductSnapshot;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderBusiness orderBusiness;
    private final ModelMapper modelMapper;
    private final CustomerBusiness customerBusiness;
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final OrderItemBusiness orderItemBusiness;
    private final ProductBusiness productBusiness;

    @Autowired
    public OrderServiceImpl(OrderBusiness orderBusiness,
                            ModelMapper modelMapper,
                            CustomerBusiness customerBusiness,
                            ShippingAddressBusiness shippingAddressBusiness,
                            OrderItemBusiness orderItemBusiness,
                            ProductBusiness productBusiness) {
        this.orderBusiness = orderBusiness;
        this.modelMapper = modelMapper;
        this.customerBusiness = customerBusiness;
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.orderItemBusiness = orderItemBusiness;
        this.productBusiness = productBusiness;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderRequest orderRequest) {
        log.debug("createOrder() Start | request: {}", orderRequest);
        try {

            // Cần implement Current context ở đây
//            Customer customer = customerBusiness.getCurrentCustomer()
//                    .orElseThrow(() -> new AppException(401, "Unauthorized"));


            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(orderRequest.getShippingAddressId()))
                    .orElseThrow(() -> new AppException(404, "Shipping address not found"));
            AddressSnapshot addressSnapshot = new AddressSnapshot(shippingAddress);

            var order = modelMapper.map(orderRequest, Order.class);
            order.setId(UUID.randomUUID());
            order.setStatus(OrderStatus.ORDER_REQUESTED);
            order.setCustomer(null);
            order.setShippingAddress(addressSnapshot);

            List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                    .map(itemRequest -> {
                        Product product = productBusiness.getById(UUID.fromString(itemRequest.getProductId()))
                                .orElseThrow(() -> new AppException(404, "Product not found"));

                        OrderItem item = new OrderItem();
                        item.setProduct(new ProductSnapshot(product));
                        item.setQuantity(itemRequest.getQuantity());
                        item.setOrder(order);
                        return item;
                    }).collect(Collectors.toList());
            order.setOrderItems(orderItems);
            var totalPrice = 0.0;
            for(OrderItem orderItem : orderItems) {
                totalPrice += orderItem.getProduct().getPrice() * orderItem.getQuantity();
            }
            order.setTotalPrice(totalPrice);
            log.debug("createOrder() End | orderId: {}", order.getId());
            return modelMapper.map(orderBusiness.create(order), OrderDTO.class);
        } catch (Exception e) {
            log.error("createOrder() Exception | message: {}", e.getMessage(), e);
            throw new AppException(500, "Failed to create order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(OrderRequest orderRequest, UUID orderId) {
        log.debug("updateOrder() Start | orderId: {}, request: {}", orderId, orderRequest);
        try {
            Order existingOrder = orderBusiness.getById(orderId)
                    .orElseThrow(() -> new AppException(404, "Order not found"));

            //Sample, can tham khao them
            existingOrder.setStatus(OrderStatus.ARRIVED_IN_DESTINATION);

            Order updatedOrder = orderBusiness.update(existingOrder);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        } catch (Exception e) {
            log.error("updateOrder() Exception | orderId: {}, message: {}", orderId, e.getMessage(), e);
            throw new AppException(500, "Failed to update order");
        }
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        log.debug("getOrderById() Start | orderId: {}", orderId);
        try {
            Order order = orderBusiness.getById(orderId)
                    .orElseThrow(() -> new AppException(404, "Order not found"));
            return modelMapper.map(order, OrderDTO.class);
        } catch (Exception e) {
            log.error("getOrderById() Exception | orderId: {}, message: {}", orderId, e.getMessage(), e);
            throw new AppException(500, "Failed to get order");
        }
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        log.debug("getAllOrders() Start");
        try {
            return orderBusiness.getAll().stream()
                    .map(order -> modelMapper.map(order, OrderDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getAllOrders() Exception | message: {}", e.getMessage(), e);
            throw new AppException(500, "Failed to get orders");
        }
    }

    @Override
    public boolean deleteOrder(UUID orderId) {
        log.debug("deleteOrder() Start | orderId: {}", orderId);
        try {
            orderBusiness.delete(orderId);
            return true;
        } catch (Exception e) {
            log.error("deleteOrder() Exception | orderId: {}, message: {}", orderId, e.getMessage(), e);
            throw new AppException(500, "Failed to delete order");
        }
    }
}