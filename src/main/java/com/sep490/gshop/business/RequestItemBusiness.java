package com.sep490.gshop.business;

import com.sep490.gshop.entity.RequestItem;

import java.util.List;
import java.util.UUID;

public interface RequestItemBusiness extends BaseBusiness<RequestItem> {
    List<RequestItem> findAllById(List<String> ids);

    List<RequestItem> findAllBySubRequestId(UUID id);
}
