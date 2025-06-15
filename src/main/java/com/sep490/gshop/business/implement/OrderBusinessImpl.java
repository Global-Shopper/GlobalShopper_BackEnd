package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderBusinessImpl extends BaseBusinessImpl<Order, OrderRepository> implements OrderBusiness {
    protected OrderBusinessImpl(OrderRepository repository) {
        super(repository);
    }
}
