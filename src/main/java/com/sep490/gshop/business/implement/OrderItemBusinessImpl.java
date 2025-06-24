package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BaseBusiness;
import com.sep490.gshop.business.OrderItemBusiness;
import com.sep490.gshop.entity.OrderItem;
import com.sep490.gshop.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemBusinessImpl extends BaseBusinessImpl<OrderItem, OrderItemRepository> implements OrderItemBusiness {
    protected OrderItemBusinessImpl(OrderItemRepository repository) {
        super(repository);
    }
}
