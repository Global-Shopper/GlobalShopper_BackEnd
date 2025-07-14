package com.sep490.gshop.business;

import com.sep490.gshop.entity.RequestItem;

import java.util.List;

public interface RequestItemBusiness extends BaseBusiness<RequestItem> {
    List<RequestItem> findAllById(List<String> ids);
}
