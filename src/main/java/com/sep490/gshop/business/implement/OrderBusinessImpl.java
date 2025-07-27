package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.OrderBusiness;
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
    public Page<Order> getOrdersByCustomerId(UUID id, Pageable pageable) {
        return repository.findByCustomerId(id, pageable);
    }

    @Override
    public Page<Order> getUnassignedOrders(Pageable pageable) {
        return repository.findByAdminIsNull(pageable);
    }

    @Override
    public Page<Order> getAssignedOrdersByAdminId(UUID id, Pageable pageable) {
        return repository.findByAdminId(id, pageable);
    }
}
