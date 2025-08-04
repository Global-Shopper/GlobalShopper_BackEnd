package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderBusinessImpl extends BaseBusinessImpl<Order, OrderRepository> implements OrderBusiness {
    protected OrderBusinessImpl(OrderRepository repository) {
        super(repository);
    }

    @Override
    public Page<Order> getOrdersByCustomerId(UUID id, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return repository.findByCustomerIdAndStatus(id, status, pageable);
        }
        return repository.findByCustomerId(id, pageable);
    }

    @Override
    public Page<Order> getAssignedOrdersByAdminId(UUID id, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return repository.findByAdminIdAndStatus(id, status, pageable);
        }
        return repository.findByAdminId(id, pageable);
    }

    @Override
    public Order findByTrackingNumber(String trackingNumber, String deliveryCode) {
        return repository.findByTrackingNumberAndShippingCarrier(trackingNumber, deliveryCode.toLowerCase());
    }
}
