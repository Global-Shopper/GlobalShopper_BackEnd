package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.RequestItemDTO;

import java.util.UUID;

public interface RequestItemService {
    RequestItemDTO getRequestItem(UUID id);
}
