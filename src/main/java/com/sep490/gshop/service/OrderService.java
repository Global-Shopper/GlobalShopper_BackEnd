package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.OrderDTO;

import java.util.UUID;

public interface OrderService {
        OrderDTO FindById(UUID id);
}
